package me.ccrama.Trails.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import de.diddiz.LogBlock.Actor;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
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
        if (!event.hasBlock() || !(event.getHand() == EquipmentSlot.HAND) || !event.hasItem() || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (event.getItem().getType() == Trails.config.trailTool && event.getPlayer().hasPermission("trails.trail-tool")) {
            Block block = event.getClickedBlock();
            Link link = plugin.getMoveEventListener().getLink(block);
            if (link != null && (plugin.getMoveEventListener().checkConditions(event.getPlayer(), event.getClickedBlock().getLocation()) || event.getPlayer().hasPermission("trails.trail-tool.bypass-protection"))) {
                plugin.getMoveEventListener().makePath(event.getPlayer(), block, link, true);
            }
            Material material = event.getItem().getType();
            if (material == Material.DIAMOND_SHOVEL || material == Material.IRON_SHOVEL || material == Material.GOLDEN_SHOVEL || material == Material.WOODEN_SHOVEL || material == Material.NETHERITE_SHOVEL || material == Material.STONE_SHOVEL)
                event.setCancelled(true);
        } else if (event.getItem().getType() == Trails.config.infoTool && event.getPlayer().hasPermission("trails.info-tool")) {
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
}
