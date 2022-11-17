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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class MoveEventListener implements Listener {

    private final Trails main;

    public static final HashMap<UUID, Booster> speedBoostedPlayers = new HashMap<>();
    public static BukkitTask boosterTask;

    public MoveEventListener(Trails plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void walk(PlayerMoveEvent e) {
        if (e.getFrom().getBlock().equals(e.getTo().getBlock())) return;

        Block block = e.getFrom().subtract(0.0D, 0.1D, 0.0D).getBlock();
        Link link = this.main.getConfigManager().getLinksConfig().getLinks().getFromMat(block.getType());
        Player p = e.getPlayer();

        //System.out.println(p.getWalkSpeed());

        Booster booster = speedBoostedPlayers.get(p.getUniqueId());
        float targetSpeed = 0.2F;
        if(link != null) targetSpeed = 0.2F * link.getSpeedBoost();

        if (booster !=null && p.getWalkSpeed() != targetSpeed && block.getType() != Material.AIR) {
            booster.setTargetSpeed(targetSpeed);
            return;
        }else if(!p.hasPermission("trails.bypass-speed-boost") && booster == null && p.getWalkSpeed() != targetSpeed && block.getType() != Material.AIR){
            boolean skip = false;
            if(main.getConfigManager().onlyTrails){
                NamespacedKey key = new NamespacedKey(main, "walks");
                PersistentDataContainer container = new CustomBlockData(block, main);

                if(!container.has(new NamespacedKey(main, "trail"), PersistentDataType.BYTE)){
                    skip = true;
                }
            }

            if(!skip) {
                Booster playerBooster = new Booster(targetSpeed, p);
                speedBoostedPlayers.put(p.getUniqueId(), playerBooster);
                if (boosterTask == null || boosterTask.isCancelled()) boosterTask = new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Map.Entry<UUID, Booster> entry : speedBoostedPlayers.entrySet()) {
                            Player player = entry.getValue().getPlayer();
                            if (player == null) removeBoostedPlayer(player.getUniqueId(), true);
                            else {
                                if (entry.getValue().getTargetSpeed() > player.getWalkSpeed())
                                    player.setWalkSpeed(Math.min(player.getWalkSpeed() + main.getConfigManager().speedBoostStep, entry.getValue().getTargetSpeed()));
                                else
                                    player.setWalkSpeed(Math.max(player.getWalkSpeed() - main.getConfigManager().speedBoostStep, entry.getValue().getTargetSpeed()));
                            }

                            if (player.getWalkSpeed() == entry.getValue().getTargetSpeed())
                                removeBoostedPlayer(entry.getKey(), false);
                        }
                    }
                }.runTaskTimer(main, 0L, main.getConfigManager().speedBoostInterval);
            }
        }

        if ((main.getConfigManager().sneakBypass && e.getPlayer().isSneaking()) || link == null) return;

        if ((!main.getConfigManager().usePermission && main.getToggles().isDisabled(p)) || (main.getConfigManager().usePermission && !p.hasPermission("trails.create-trails"))) {
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
        // Substract 0.1 because sometimes it is not a full block (e.g. Path)
        makePath(p, block, link);
    }

    private void makePath(Player p, Block block, Link link) {
        double foo = Math.random() * 100.0D;
        double bar = (double) link.chanceOccurance();
        if (p.isSprinting()) bar *= main.getConfigManager().runModifier;

        if (foo > bar) return;

        NamespacedKey key = new NamespacedKey(main, "walks");
        PersistentDataContainer container = new CustomBlockData(block, main);

        Integer walked = container.get(key, PersistentDataType.INTEGER);
        if (walked == null) walked = 0;

        if (walked >= link.decayNumber()) {
            container.set(key, PersistentDataType.INTEGER, 0);
            try {
                NamespacedKey key2 = new NamespacedKey(main, "trail");
                container.set(key2, PersistentDataType.BYTE, (byte)1);
                this.changeNext(p, block, link);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                e.printStackTrace();
            }
        } else container.set(key, PersistentDataType.INTEGER, walked + 1);
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

    public static void removeBoostedPlayer(UUID uuid, boolean defaultSpeed){
        Player player =  Trails.getInstance().getServer().getPlayer(uuid);
        if(player != null && defaultSpeed) player.setWalkSpeed(0.2F);
        speedBoostedPlayers.remove(uuid);
        if(speedBoostedPlayers.size() == 0 && boosterTask != null && !boosterTask.isCancelled()){
            boosterTask.cancel();
        }
    }

    public static class Booster{
        float targetSpeed;
        Player player;

        public Booster(float targetSpeed, Player player) {
            this.targetSpeed = targetSpeed;
            this.player = player;
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

        public void setPlayer(Player player) {
            this.player = player;
        }
    }

}
	