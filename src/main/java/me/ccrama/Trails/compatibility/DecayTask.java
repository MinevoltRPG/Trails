package me.ccrama.Trails.compatibility;

import com.jeff_media.customblockdata.CustomBlockData;
import de.diddiz.LogBlock.Actor;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.objects.Link;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class DecayTask {

    private static Trails plugin = null;
    private static NamespacedKey walksKey = null;
    private static NamespacedKey trailKey = null;
    private BukkitTask decayTask;

    public DecayTask(Trails plugin){
        this.plugin = plugin;
        walksKey = new NamespacedKey(plugin, "w");
        trailKey = new NamespacedKey(plugin, "n");
        decayTask = new BukkitRunnable(){
            @Override
            public void run() {
                List<World> worlds = plugin.getServer().getWorlds();
                List<Block> decayBlocks = new ArrayList<>();
                Random random = new Random();
                for(World world : worlds){
                    if (!plugin.getConfigManager().allWorldsEnabled && !plugin.getConfigManager().enabledWorlds.contains(world.getName())) continue;
                    List<Chunk> chunks = Arrays.asList(world.getLoadedChunks());
                    for(Chunk chunk : chunks){
                        List<Location> playerLocations = plugin.getServer().getOnlinePlayers().stream().map(Player::getLocation).filter(s -> s.getWorld().equals(world)).collect(Collectors.toList());
                        if(random.nextDouble() > plugin.getConfigManager().chunkChance) continue;
                        List<Block> blocks = getRandomBlocks(chunk, plugin.getConfigManager().decayFraction);
                        if(blocks == null) continue;
                        for(Block block : blocks) {
                            boolean skip = false;
                            for (Location location : playerLocations) {
                                if(location.distance(block.getLocation()) < plugin.getConfigManager().decayDistance){
                                    skip = true;
                                    break;
                                }
                            }
                            if(skip) continue;
                            decayBlocks.add(block);
                        }
                    }
                }

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        decayManyBlocks(decayBlocks);
                    }
                }.runTaskLater(plugin, 0L);
            }
        }.runTaskTimerAsynchronously(plugin, plugin.getConfigManager().decayTimer, plugin.getConfigManager().decayTimer);
    }

    private void decayManyBlocks(List<Block> blocks){
        for(Block block : blocks) decayBlock(block);
    }

    public static void decayBlock(Block block){
        Link link = plugin.getMoveEventListener().getLink(block);
        if(link == null || (plugin.getWorldGuardHook() != null && plugin.getConfigManager().wgDecayFlag && !plugin.getWorldGuardHook().canDecay(block.getLocation()))) return;
        decayPath(block, link);
    }

    public static void decayPath(Block block, Link link) {
            PersistentDataContainer container = new CustomBlockData(block, plugin);
            Integer walked = container.get(walksKey, PersistentDataType.INTEGER);
            if (walked == null) return;
            int newWalked = walked - Math.max(1, (int)(walked*plugin.getConfigManager().stepDecayFraction));
            Link previous = link.getPrevious();
            boolean firstLink = previous == null;
            Link superPrevious = null;
            if(!firstLink) superPrevious = previous.getPrevious();
            if (newWalked < 0) {
                if(superPrevious == null && previous != null){
                    container.set(walksKey, PersistentDataType.INTEGER, Math.max(0, previous.decayNumber() - 1));
                    container.remove(trailKey);
                } else if(superPrevious != null) {
                    container.set(walksKey, PersistentDataType.INTEGER, Math.max(0, previous.decayNumber() - 1));
                    container.set(trailKey, PersistentDataType.STRING, link.getTrailName() + ":" + link.getPrevious().identifier());
                } else {
                    container.remove(trailKey);
                    container.remove(walksKey);
                    return;
                }
                try {
                    changePrevious(block, link);
                } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else{
                if(previous == null && newWalked == 0){
                    container.remove(trailKey);
                    container.remove(walksKey);
                } else container.set(walksKey, PersistentDataType.INTEGER, newWalked);
            }
    }

    public static  void changePrevious(Block block, Link link) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Material type = block.getType();
        BlockState state = block.getState();
        BlockData data = block.getBlockData();
        if (link.getPrevious() != null) {
            Material prevMat = link.getPrevious().getMat();
            block.setType(prevMat);
            block.getState().update(true);
            //log block changes in LogBlock and CoreProtect
            if (plugin.getLbHook() != null && plugin.getConfigManager().logBlock) {
                plugin.getLbHook().getLBConsumer().queueBlockReplace(new Actor("NaturalTrailDecay"), state, block.getState());
            }
            if (plugin.getCpHook() != null && plugin.getConfigManager().coreProtect) {
                plugin.getCpHook().getAPI().logRemoval("NaturalTrailDecay", block.getLocation(), type, data);
                plugin.getCpHook().getAPI().logPlacement("NaturalTrailDecay", block.getLocation(), prevMat, block.getBlockData());
            }
            plugin.triggerUpdate(block.getLocation());
        }
    }

    private List<Block> getRandomBlocks(Chunk chunk, double percentage){
        List<Block> blocks = new ArrayList<>(CustomBlockData.getBlocksWithCustomData(plugin, chunk));
        if(blocks.size() == 0) return null;
        Random rand = new Random();

        List<Block> result = new ArrayList<>();
        double lastChance = blocks.size()*percentage - (int)(blocks.size()*percentage);
        int repeat = Math.max(1, (int)(blocks.size()*percentage));
        for(int i=0;i<repeat;i++){
            int id = rand.nextInt(blocks.size());
            Block block = blocks.get(id);
            if(i == repeat - 1 && rand.nextDouble() > lastChance) continue;
            result.add(block);
            blocks.remove(id);
        }
        return result;
    }

    public void stopTask(){
        if(this.decayTask != null && !this.decayTask.isCancelled()) this.decayTask.cancel();
    }

    public static void setPlugin(Trails plugin) {
        DecayTask.plugin = plugin;
    }

    public static void setWalksKey(NamespacedKey walksKey) {
        DecayTask.walksKey = walksKey;
    }

    public static void setTrailKey(NamespacedKey trailKey) {
        DecayTask.trailKey = trailKey;
    }
}
