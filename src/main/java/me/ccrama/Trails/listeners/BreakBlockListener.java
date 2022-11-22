package me.ccrama.Trails.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class BreakBlockListener implements Listener {

    private final Trails main;

    public BreakBlockListener(Trails plugin) {
        this.main = plugin;
    }

    @EventHandler
    public void breakBlock(BlockBreakEvent event) {
        final PersistentDataContainer customBlockData = new CustomBlockData(event.getBlock(), main);
        if (customBlockData.has(new NamespacedKey(main, "w"), PersistentDataType.INTEGER)) customBlockData.remove(new NamespacedKey(main, "w"));
        if (customBlockData.has(new NamespacedKey(main, "n"), PersistentDataType.STRING)) customBlockData.remove(new NamespacedKey(main, "n"));
    }

}
