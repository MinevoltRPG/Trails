package me.ccrama.Trails.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import de.diddiz.LogBlock.Actor;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

public class PlayerInteractListener implements Listener {

    Trails plugin;

    public PlayerInteractListener(Trails plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void PlayerInteract(PlayerInteractEvent event) {
        if (!event.hasBlock() || !(event.getHand() == EquipmentSlot.HAND) || !event.hasItem()) return;
        if (event.getItem().getType() == plugin.getConfigManager().trailTool && event.getPlayer().hasPermission("trails.trail-tool")) {
            Block block = event.getClickedBlock();
            Link link = getLink(block);
            if (link != null && (event.getPlayer().hasPermission("trails.trail-tool.bypass-protection") || checkConditions(event.getPlayer()))) {
                makePath(event.getPlayer(), block, link);
            }
            Material material = event.getItem().getType();
            if (material == Material.DIAMOND_SHOVEL || material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL || material == Material.WOODEN_SHOVEL || material == Material.NETHERITE_SHOVEL || material == Material.STONE_SHOVEL)
                event.setCancelled(true);
        } else if (event.getItem().getType() == plugin.getConfigManager().infoTool && event.getPlayer().hasPermission("trails.info-tool")) {
            Block block = event.getClickedBlock();
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            Integer walks = container.get(new NamespacedKey(plugin, "w"), PersistentDataType.INTEGER);
            String trailName = container.get(new NamespacedKey(plugin, "n"), PersistentDataType.STRING);
            if(walks == null) walks = 0;
            if(trailName == null) trailName = "None";
            event.getPlayer().sendMessage("Amount of walks: "+walks + "\nTrail name: " + trailName);
            Material material = event.getItem().getType();
            if (material == Material.DIAMOND_SHOVEL || material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL || material == Material.WOODEN_SHOVEL || material == Material.NETHERITE_SHOVEL || material == Material.STONE_SHOVEL)
                event.setCancelled(true);
        }
    }

    private Link getLink(Block block) {
        ArrayList<Link> links = plugin.getConfigManager().getLinksConfig().getLinks().getFromMat(block.getType());
        Link link = null;

        if (links != null && links.size() == 1) link = links.get(0);
        else if (links != null) {
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            if(!container.has(new NamespacedKey(plugin, "n"), PersistentDataType.STRING)){
                Link minLink = null;
                for(Link link1 : links){
                    if(minLink == null || link1.identifier() < minLink.identifier()) minLink = link1;
                }
                link = minLink;
            }
            else {
                String[] blockTrailName = container.get(new NamespacedKey(plugin, "n"), PersistentDataType.STRING).split(":");
                Integer id = null;
                try {
                    id = Integer.parseInt(blockTrailName[1]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                link = links.get(0);
                for (Link lnk : links) {
                    //System.out.println(lnk.getTrailName()+"\n"+blockTrailName[0]+"\n"+id+"\n"+lnk.identifier());
                    if (lnk.getTrailName().equals(blockTrailName[0])) {
                        if(id != null && lnk.identifier() == id){
                            link = lnk;
                            break;
                        }
                        if(lnk.identifier() < link.identifier()) link = lnk;
                    }
                }
            }
        }
        return link;
    }

    private void makePath(Player p, Block block, Link link) {
        if(link.getNext() == null){
            p.sendMessage(plugin.getLanguage().alreadyMaxLevel);
            return;
        }
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        container.set(new NamespacedKey(plugin, "w"), PersistentDataType.INTEGER, 0);
        container.set(new NamespacedKey(plugin, "n"), PersistentDataType.STRING, link.getTrailName() + ":" + link.getNext().identifier());
        try {
            this.changeNext(p, block, link);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }

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
            if (plugin.getLbHook() != null && plugin.getConfigManager().logBlock) {
                plugin.getLbHook().getLBConsumer().queueBlockReplace(new Actor(p.getName()), state, block.getState());
            }
            if (plugin.getCpHook() != null && plugin.getConfigManager().coreProtect) {
                plugin.getCpHook().getAPI().logRemoval(p.getName(), block.getLocation(), type, data);
                plugin.getCpHook().getAPI().logPlacement(p.getName(), block.getLocation(), nextMat, block.getBlockData());
            }
        }
    }

    private boolean checkConditions(Player p) {
        // Check towny conditions
        if (plugin.getTownyHook() != null) {
            if (plugin.getTownyHook().isWilderness(p)) {
                if (!plugin.getTownyHook().isPathsInWilderness()) {
                    if (plugin.getConfigManager().sendDenyMessage)
                        sendDenyMessage(p);
                    return false;
                }
            }

            if (plugin.getTownyHook().isTownyPathsPerms()) {
                if (plugin.getTownyHook().isInHomeNation(p) && !plugin.getTownyHook().hasNationPermission(p) && !plugin.getTownyHook().isInHomeTown(p)) {
                    if (plugin.getConfigManager().sendDenyMessage)
                        sendDenyMessage(p);
                    return false;
                }
                if (plugin.getTownyHook().isInHomeTown(p) && !plugin.getTownyHook().hasTownPermission(p)) {
                    if (plugin.getConfigManager().sendDenyMessage)
                        sendDenyMessage(p);
                    return false;
                }
            } else {
                if (plugin.getTownyHook().isInOtherNation(p)) {
                    if (plugin.getConfigManager().sendDenyMessage)
                        sendDenyMessage(p);
                    return false;
                }
                if (plugin.getTownyHook().isInOtherTown(p)) {
                    if (plugin.getConfigManager().sendDenyMessage)
                        sendDenyMessage(p);
                    return false;
                }
            }
        }
        // Check Lands conditions
        if (plugin.getLandsHook() != null) {
            if (!plugin.getLandsHook().hasTrailsFlag(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (plugin.getConfigManager().sendDenyMessage)
                    sendDenyMessage(p);
                return false;
            }
        }
        // Check GriefPrevention conditions
        if (plugin.getGpHook() != null) {
            if (!plugin.getGpHook().canMakeTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (plugin.getConfigManager().sendDenyMessage)
                    sendDenyMessage(p);
                return false;
            }
        }
        // Check worldguard conditions
        if (plugin.getWorldGuardHook() != null) {
            if (!plugin.getWorldGuardHook().canCreateTrails(p, p.getLocation().subtract(0.0D, 1.0D, 0.0D))) {
                if (plugin.getConfigManager().sendDenyMessage)
                    sendDenyMessage(p);
                return false;
            }
        }
        return true;
    }

    private void sendDenyMessage(Player p) {
        p.sendMessage(plugin.getCommands().getFormattedMessage(p.getName(),plugin.getLanguage().cantCreateTrails));
    }
}
