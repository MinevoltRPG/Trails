package me.ccrama.Trails.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import de.diddiz.LogBlock.Actor;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class MoveEventListener implements Listener {


    private NamespacedKey walksKey;
    private NamespacedKey trailNameKey;
    private final Trails main;
    public static final HashMap<UUID, Booster> speedBoostedPlayers = new HashMap<>();
    public static BukkitTask boosterTask;

    public MoveEventListener(Trails plugin) {
        this.main = plugin;
        this.walksKey = new NamespacedKey(plugin, "w");
        this.trailNameKey = new NamespacedKey(plugin, "n");
    }

    @EventHandler
    public void walk(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock()) || e.getPlayer().isFlying()) return;
        if (!main.getConfigManager().allWorldsEnabled && !main.getConfigManager().enabledWorlds.contains(e.getTo().getWorld().getName()))
            return;

        PersistentDataContainer container = null;
        Block block = e.getFrom().subtract(0.0D, 0.1D, 0.0D).getBlock();
        ArrayList<Link> links = this.main.getConfigManager().getLinksConfig().getLinks().getFromMat(block.getType());
        Link link = null;

        if (block.getType() == Material.AIR) return;

        if (links != null && links.size() == 1) {
            //System.out.println("Link is 1");
            link = links.get(0);
            //System.out.println(link.getMat() + " | " + link.getTrailName());
        } else if(links != null) {
            //System.out.println("Link amount > 1");
            container = new CustomBlockData(block, main);
            String[] blockTrailName = container.get(trailNameKey, PersistentDataType.STRING).split(":");
            Integer id = null;
            try{
                id = Integer.parseInt(blockTrailName[1]);
            } catch (Exception ignored){}
            for (Link lnk : links) {
                Integer closestId = null;
                if (lnk.getTrailName().equals(blockTrailName[0])) {
                    //System.out.println("Link was found!");
                    if((id == null || lnk.identifier() == id) || (link == null && lnk.identifier() > id)){
                        link = lnk;
                        break;
                    }
                    else if(link != null && lnk.identifier() > id) break;
                    else link = lnk;
                }
            }
        }
        Player p = e.getPlayer();
        System.out.println(p.getWalkSpeed());

        //System.out.println(p.getWalkSpeed());

        if (main.getToggles().isBoost(p.getUniqueId().toString()) || (main.getConfigManager().usePermissionBoost && p.hasPermission("trails.boost"))) {
            //System.out.println("applying boost");
            boolean changeImmediately = false;

            Booster booster = speedBoostedPlayers.get(p.getUniqueId());
            float targetSpeed = 0.2F;

            // If block material is in one of the links
            if (link != null) {
                //System.out.println("Trail material");
                // In case you are on a trail material but not on an actual trail
                if (main.getConfigManager().onlyTrails) {
                    if (container == null) container = new CustomBlockData(block, main);
                    if (container.has(trailNameKey, PersistentDataType.STRING)) {
                        targetSpeed = 0.2F * link.getSpeedBoost();
                    } else changeImmediately = main.getConfigManager().immediatelyRemoveBoost;
                } else targetSpeed = 0.2F * link.getSpeedBoost();
            } else changeImmediately = main.getConfigManager().immediatelyRemoveBoost;

            //System.out.println(targetSpeed + " " + changeImmediately);
            if (p.getWalkSpeed() != targetSpeed) {
                //System.out.println("boost here");
                if (booster != null) {
                    //System.out.println("booster not null");
                    booster.setTargetSpeed(targetSpeed, changeImmediately);
                } else {
                    //System.out.println("booster null");
                    Booster playerBooster = new Booster(targetSpeed, p, changeImmediately);
                    speedBoostedPlayers.put(p.getUniqueId(), playerBooster);
                    if (boosterTask == null || boosterTask.isCancelled()) boosterTask = new BukkitRunnable() {
                        @Override
                        public void run() {
                            ArrayList<UUID> toRemove = new ArrayList<>();
                            for (Map.Entry<UUID, Booster> entry : speedBoostedPlayers.entrySet()) {
                                Player player = entry.getValue().getPlayer();
                                //System.out.println("boosting: "+player);
                                if(entry.getValue().immediately) player.setWalkSpeed(entry.getValue().targetSpeed);
                                else if (entry.getValue().getTargetSpeed() > player.getWalkSpeed())
                                    player.setWalkSpeed(Math.min(player.getWalkSpeed() + main.getConfigManager().speedBoostStep, entry.getValue().getTargetSpeed()));
                                else
                                    player.setWalkSpeed(Math.max(player.getWalkSpeed() - main.getConfigManager().speedBoostStep, entry.getValue().getTargetSpeed()));

                                if (player.getWalkSpeed() == entry.getValue().getTargetSpeed())
                                    toRemove.add(entry.getKey());
                                //
                            }

                            for (UUID uuid : toRemove) removeBoostedPlayer(uuid, false);
                        }
                    }.runTaskTimer(main, 0L, main.getConfigManager().speedBoostInterval);
                }
            }
        }

        if ((main.getConfigManager().sneakBypass && e.getPlayer().isSneaking()) || link == null) return;
        //System.out.println("asd");
        if ((!main.getConfigManager().usePermission && main.getToggles().isDisabled(p.getUniqueId().toString())) || (main.getConfigManager().usePermission && !p.hasPermission("trails.create-trails"))) {
            return;
        }
        // Check towny conditions
        if (main.getTownyHook() != null) {
            if (main.getTownyHook().isWilderness(p)) {
                if (!main.getTownyHook().isPathsInWilderness()) {
                    if (main.getConfigManager().sendDenyMessage)
                        sendDelayedMessage(p);
                    return;
                }
            }

            if (main.getTownyHook().isTownyPathsPerms()) {
                if (main.getTownyHook().isInHomeNation(p) && !main.getTownyHook().hasNationPermission(p) && !main.getTownyHook().isInHomeTown(p)) {
                    if (main.getConfigManager().sendDenyMessage)
                        sendDelayedMessage(p);
                    return;
                }
                if (main.getTownyHook().isInHomeTown(p) && !main.getTownyHook().hasTownPermission(p)) {
                    if (main.getConfigManager().sendDenyMessage)
                        sendDelayedMessage(p);
                    return;
                }
            } else {
                if (main.getTownyHook().isInOtherNation(p)) {
                    if (main.getConfigManager().sendDenyMessage)
                        sendDelayedMessage(p);
                    return;
                }
                if (main.getTownyHook().isInOtherTown(p)) {
                    if (main.getConfigManager().sendDenyMessage)
                        sendDelayedMessage(p);
                    return;
                }
            }
        }
        // Check Lands conditions
        if (main.getLandsHook() != null) {
            if (!main.getLandsHook().hasTrailsFlag(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (main.getConfigManager().sendDenyMessage)
                    sendDelayedMessage(p);
                return;
            }
        }
        // Check GriefPrevention conditions
        if (main.getGpHook() != null) {
            if (!main.getGpHook().canMakeTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (main.getConfigManager().sendDenyMessage)
                    sendDelayedMessage(p);
                return;
            }
        }
        // Check worldguard conditions
        if (main.getWorldGuardHook() != null) {
            if (!main.getWorldGuardHook().canCreateTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (main.getConfigManager().sendDenyMessage)
                    sendDelayedMessage(p);
                return;
            }
        }
        // Subtract 0.1 because sometimes it is not a full block (e.g. Path)
        makePath(p, block, link);
    }

    private void makePath(Player p, Block block, Link link) {
        double foo = Math.random() * 100.0D;
        double bar = link.chanceOccurance();
        if (p.isSprinting()) bar *= main.getConfigManager().runModifier;
        if (foo > bar) return;

        PersistentDataContainer container = new CustomBlockData(block, main);
        Integer walked = container.get(walksKey, PersistentDataType.INTEGER);
        if (walked == null) walked = 0;
        //System.out.println(walked);
        if (walked >= link.decayNumber()) {
            container.set(walksKey, PersistentDataType.INTEGER, 0);
            container.set(trailNameKey, PersistentDataType.STRING, link.getTrailName()+":"+link.identifier());
            try {
                this.changeNext(p, block, link);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else container.set(walksKey, PersistentDataType.INTEGER, walked + 1);
    }

    private void changeNext(Player p, Block block, Link link) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Material type = block.getType();
        BlockState state = block.getState();
        BlockData data = block.getBlockData();
        if (link.getNext() != null) {
            Material nextMat = link.getNext().getMat();
            block.setType(nextMat);
            block.getState().update(true);
            //log block changes in LogBlock and CoreProtect
            if (main.getLbHook() != null && main.getConfigManager().logBlock) {
                main.getLbHook().getLBConsumer().queueBlockReplace(new Actor(p.getName()), state, block.getState());
            }
            if (main.getCpHook() != null && main.getConfigManager().coreProtect) {
                main.getCpHook().getAPI().logRemoval(p.getName(), block.getLocation(), type, data);
                main.getCpHook().getAPI().logPlacement(p.getName(), block.getLocation(), nextMat, block.getBlockData());
            }
        }
    }

    private void sendDelayedMessage(Player p) {
        if (!main.messagePlayers.contains(p.getUniqueId())) {
            p.sendMessage(main.getCommands().getFormattedMessage(p.getName(), main.getLanguage().cantCreateTrails));
            main.messagePlayers.add(p.getUniqueId());
            Bukkit.getScheduler().runTaskLater(main, () -> delayWGMessage(p.getUniqueId()), 20 * main.getConfigManager().messageInterval);
        }
    }

    private void delayWGMessage(UUID id) {
        if (main.messagePlayers.contains(id)) {
            main.messagePlayers.remove(id);
        }
    }

    public static void removeBoostedPlayer(UUID uuid, boolean defaultSpeed) {
        Player player = Trails.getInstance().getServer().getPlayer(uuid);
        if (player != null && defaultSpeed) player.setWalkSpeed(0.2F);
        speedBoostedPlayers.remove(uuid);
        if (speedBoostedPlayers.size() == 0 && boosterTask != null && !boosterTask.isCancelled()) {
            boosterTask.cancel();
        }
    }

    public static void disableBoostTask() {
        if (boosterTask == null || boosterTask.isCancelled()) return;
        for (Booster booster : speedBoostedPlayers.values()) {
            Player player = booster.getPlayer();
            if (player == null) continue;
            player.setWalkSpeed(0.2F);
        }
        boosterTask.cancel();
    }

    public static class Booster {
        float targetSpeed;
        Player player;
        boolean immediately;

        public Booster(float targetSpeed, Player player) {
            this.targetSpeed = targetSpeed;
            this.player = player;
            immediately = false;
        }

        public Booster(float targetSpeed, Player player, boolean immediately) {
            this.targetSpeed = targetSpeed;
            this.player = player;
            this.immediately = immediately;
        }

        public float getTargetSpeed() {
            return targetSpeed;
        }

        public Player getPlayer() {
            return player;
        }

        public void setTargetSpeed(float targetSpeed) {
            this.targetSpeed = targetSpeed;
        }

        public void setTargetSpeed(float targetSpeed, boolean immediately) {
            this.targetSpeed = targetSpeed;
            this.immediately = immediately;
        }

        public void setPlayer(Player player) {
            this.player = player;
        }
    }

}
	