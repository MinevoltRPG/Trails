package me.ccrama.Trails.util;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.checkerframework.checker.units.qual.C;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class ParticleUtil {

    private static Map<Player, BukkitTask> showTrailsTasks = new HashMap<>();
    private static Map<Player, BukkitTask> showCuboidTasks = new HashMap<>();

    public static Set<Block> getTrailBlocks(Location center, double sqRadius) {
        Set<Block> blocks = new HashSet<>();
        World world = center.getWorld();
        if (world == null) return null;
        Location location = center.clone();
        int xMin = location.add(-sqRadius, 0, -sqRadius).getChunk().getX();
        int zMin = location.getChunk().getZ();
        int xMax = location.add(2 * sqRadius, 0, 2 * sqRadius).getChunk().getX();
        int zMax = location.getChunk().getZ();
        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                Set<Block> blockSet = CustomBlockData.getBlocksWithCustomData(Trails.getInstance(), world.getChunkAt(x, z));
                blockSet = blockSet.stream().filter(b -> {
                    CustomBlockData customBlockData = new CustomBlockData(b, Trails.getInstance());
                    return (Math.abs(b.getX() - center.getX()) <= sqRadius && Math.abs(b.getZ() - center.getZ()) <= sqRadius)
                            && customBlockData.has(new NamespacedKey(Trails.getInstance(), "n"));
                }).collect(Collectors.toSet());
                blocks.addAll(blockSet);
            }
        }
        return blocks;
    }

    public static List<Location> getTrailTopLocations(@Nullable Set<Block> blocks) {
        int steps = 8;
        List<Location> locations = new ArrayList<>();
        if (blocks == null) return locations;
        for (Block block : blocks) {
            CustomBlockData customBlockData = new CustomBlockData(block, Trails.getInstance());
            if (!customBlockData.has(new NamespacedKey(Trails.getInstance(), "n"))) continue;
            Location location = block.getLocation().add(0, 1, 0);
            Block xUp = block.getRelative(-1, 0, 0);
            if (!blocks.contains(xUp)) {
                Location tmp = location.clone();
                for (int i = 0; i <= steps; i++) {
                    locations.add(tmp.clone().add(0, 0, 1.0 / steps * i));
                }
            }

            Block xDown = block.getRelative(1, 0, 0);
            if (!blocks.contains(xDown)) {
                Location tmp = location.clone().add(1, 0, 0);
                for (int i = 0; i <= steps; i++) {
                    locations.add(tmp.clone().add(0, 0, 1.0 / steps * i));
                }
            }

            Block yUp = block.getRelative(0, 0, -1);
            if (!blocks.contains(yUp)) {
                Location tmp = location.clone();
                for (int i = 0; i <= steps; i++) {
                    locations.add(tmp.clone().add(1.0 / steps * i, 0, 0));
                }
            }

            Block yDown = block.getRelative(0, 0, 1);
            if (!blocks.contains(yDown)) {
                Location tmp = location.clone().add(0, 0, 1);
                for (int i = 0; i <= steps; i++) {
                    locations.add(tmp.clone().add(1.0 / steps * i, 0, 0));
                }
            }
        }
        return locations;
    }

    public static void showTrailBlocks(Player player, Location location, double chRadius, ParticleEffect particleEffect) {
        final Set<Block>[] blocks = new Set[]{getTrailBlocks(location, chRadius)};
        if(blocks[0] == null) return;
        final List<Location>[] locations = new List[]{getTrailTopLocations(getTrailBlocks(location, chRadius))};
        final int[] counter = {0};
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                Set<Block> newBlocks = getTrailBlocks(location, chRadius);
                if(newBlocks == null){
                    this.cancel();
                    return;
                }
                if(!(newBlocks.containsAll(blocks[0]) && newBlocks.size() == blocks[0].size())){
                    locations[0] = getTrailTopLocations(newBlocks);
                    blocks[0] = newBlocks;
                }
                for (Location loc : locations[0]) {
                    new ParticleBuilder(particleEffect, loc).setAmount(1).setSpeed(0f).setOffset(0.0f, 0.0f, 0.0f).display(player);
                }
                counter[0]++;
                if (counter[0] > 30) {
                    this.cancel();
                }
            }
        }.runTaskTimerAsynchronously(Trails.getInstance(), 0L, 10L);
        if (showTrailsTasks.containsKey(player) && !showTrailsTasks.get(player).isCancelled())
            showTrailsTasks.get(player).cancel();
        showTrailsTasks.put(player, task);
    }

    public static List<Location> getCuboidLocations(Block block1, Block block2){
        List<Location> locations = new ArrayList<>();
        int steps = 2;
        Location loc = new Location(block1.getWorld(),
                Math.min(block1.getX(), block2.getX()),
                Math.min(block1.getY(), block2.getY()),
                Math.min(block1.getZ(), block2.getZ()));
        int dx = Math.abs(block1.getX() - block2.getX()) +1;
        int dz = Math.abs(block1.getZ() - block2.getZ()) +1;
        int dy = Math.abs(block1.getY() - block2.getY()) +1;
        for(int i = 0; i<=dx*steps; i++){
            Location loc1 = loc.clone().add(1.0/steps*i, 0, 0);
            locations.add(loc1);

            Location loc2 = loc.clone().add(1.0/steps*i, dy, 0);
            locations.add(loc2);

            Location loc3 = loc.clone().add(1.0/steps*i, dy, dz);
            locations.add(loc3);

            Location loc4 = loc.clone().add(1.0/steps*i, 0, dz);
            locations.add(loc4);
        }

        for(int i=0;i<=dy*steps;i++){
            Location loc1 = loc.clone().add(0, 1.0/steps*i, 0);
            locations.add(loc1);

            Location loc2 = loc.clone().add(dx, 1.0/steps*i, 0);
            locations.add(loc2);

            Location loc3 = loc.clone().add(dx, 1.0/steps*i, dz);
            locations.add(loc3);

            Location loc4 = loc.clone().add(0, 1.0/steps*i, dz);
            locations.add(loc4);
        }

        for(int i=0;i<=dz*steps;i++){
            Location loc1 = loc.clone().add(0, 0, 1.0/steps*i);
            locations.add(loc1);

            Location loc2 = loc.clone().add(dx, 0, 1.0/steps*i);
            locations.add(loc2);

            Location loc3 = loc.clone().add(dx, dy, 1.0/steps*i);
            locations.add(loc3);

            Location loc4 = loc.clone().add(0, dy, 1.0/steps*i);
            locations.add(loc4);
        }

        return locations;
    }

    public static void showCuboid(Block block1, Block block2, Player player, ParticleEffect particleEffect){
        List<Location> locations = getCuboidLocations(block1, block2);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if(player == null || !player.isOnline()){
                    this.cancel();
                    return;
                }
                for(Location location : locations){
                    new ParticleBuilder(particleEffect, location).setSpeed(0f).setOffset(0f,0f,0f).setAmount(1).display(player);
                }
            }
        }.runTaskTimerAsynchronously(Trails.getInstance(), 0L, 10L);
        if(showCuboidTasks.containsKey(player) && !showCuboidTasks.get(player).isCancelled()) showCuboidTasks.get(player).cancel();
        showCuboidTasks.put(player, task);
    }

    public static void stopShowingCuboid(Player player){
        if(showCuboidTasks.containsKey(player) && !showCuboidTasks.get(player).isCancelled()){
            showCuboidTasks.get(player).cancel();
            showCuboidTasks.remove(player);
        }
    }

}
