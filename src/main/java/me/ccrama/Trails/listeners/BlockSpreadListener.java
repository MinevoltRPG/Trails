package me.ccrama.Trails.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.compatibility.DecayTask;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BlockSpreadListener implements Listener {

    private Trails plugin;
    private NamespacedKey walksKey;

    public BlockSpreadListener(Trails plugin){
        this.plugin = plugin;
        this.walksKey = new NamespacedKey(plugin, "w");
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent event){
        if(event.getNewState().getType() != Material.GRASS_BLOCK) return;
        if(Trails.config.getLinksConfig().getLinks().getFromMat(Material.DIRT) == null) return;

        Block block = event.getBlock();
        PersistentDataContainer container = new CustomBlockData(block, plugin);
        if(container.has(walksKey, PersistentDataType.INTEGER)){
            DecayTask.decayBlock(block);
        }
    }
}
