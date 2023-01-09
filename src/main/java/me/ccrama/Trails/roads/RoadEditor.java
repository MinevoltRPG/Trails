package me.ccrama.Trails.roads;

import me.ccrama.Trails.Trails;
import me.ccrama.Trails.util.Pair;
import me.ccrama.Trails.util.ParticleUtil;
import me.ccrama.Trails.util.RoadUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import xyz.xenondevs.particle.ParticleBuilder;
import xyz.xenondevs.particle.ParticleEffect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadEditor implements Listener {
    Player player;
    BukkitTask particleTask;
    BukkitTask stairsTask;
    Block block1;
    Block block2;
    Road road;

    List<Block> supportBlocks = new ArrayList<>();
    Map<Block, Pair<Material, BlockData>> replaceBlocks = new HashMap<>();

    public RoadEditor(Player player, Block block1, Road road) {
        this.player = player;
        this.block1 = block1;
        this.block2 = block1.getRelative(road.x - 1, road.y - 1, road.z - 1);
        this.road = road;
        Bukkit.getPluginManager().registerEvents(this, Trails.getInstance());
        setupEditor();
        createTask();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!isInCuboid(event.getBlockPlaced())){
            if(isInStairs(event.getBlock())){
                if (!event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    return;
                }
                RoadBlock roadBlock = new RoadBlock();
                RoadBlockType roadBlockType = new RoadBlockType(event.getBlockPlaced().getType(), 1.0, null, null, event.getBlockPlaced().getBlockData().getAsString());
                roadBlock.addMaterial(roadBlockType);
                int x = event.getBlock().getX() - block1.getX();
                int y = event.getBlockPlaced().getY() - block1.getY();
                if(road.stairBlock[x] != null){
                    event.getPlayer().sendMessage("Already has stair");
                    event.setCancelled(true);
                    return;
                }
                road.stairBlock[x] = roadBlock;
                road.stairsY[x] = y;
                new ParticleBuilder(ParticleEffect.END_ROD, event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5)).setAmount(5).setOffset(0.2f, 0.2f, 0.2f).setSpeed(0.2f).display(player);
            }
            return;
        }
        if (!event.getPlayer().equals(player)) {
            event.setCancelled(true);
            return;
        }
        RoadBlock roadBlock = new RoadBlock();
        RoadBlockType roadBlockType = new RoadBlockType(event.getBlockPlaced().getType(), 1.0, null, null, event.getBlockPlaced().getBlockData().getAsString());
        roadBlock.addMaterial(roadBlockType);
        int x = event.getBlockPlaced().getX() - block1.getX();
        int z = event.getBlockPlaced().getZ() - block1.getZ();
        int y = event.getBlockPlaced().getY() - block1.getY();
        road.setRoadBlock(x, y, z, roadBlock);
        new ParticleBuilder(ParticleEffect.END_ROD, event.getBlockPlaced().getLocation().clone().add(0.5, 0.5, 0.5)).setAmount(5).setOffset(0.2f, 0.2f, 0.2f).setSpeed(0.2f).display(player);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!isInCuboid(event.getBlock())){
            if(isInStairs(event.getBlock())){
                if (!event.getPlayer().equals(player)) {
                    event.setCancelled(true);
                    return;
                }
                RoadBlock roadBlock = new RoadBlock();
                RoadBlockType roadBlockType = new RoadBlockType(Material.AIR, 1.0, null, null, "");
                roadBlock.addMaterial(roadBlockType);
                int x = event.getBlock().getX() - block1.getX();
                road.stairBlock[x] = null;
                new ParticleBuilder(ParticleEffect.END_ROD, event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5)).setAmount(5).setOffset(0.2f, 0.2f, 0.2f).setSpeed(0.2f).display(player);
            }
            return;
        }
        if (!event.getPlayer().equals(player)) {
            event.setCancelled(true);
            return;
        }
        RoadBlock roadBlock = new RoadBlock();
        RoadBlockType roadBlockType = new RoadBlockType(Material.AIR, 1.0, null, null, "");
        roadBlock.addMaterial(roadBlockType);
        int x = event.getBlock().getX() - block1.getX();
        int z = event.getBlock().getZ() - block1.getZ();
        int y = event.getBlock().getY() - block1.getY();
        road.setRoadBlock(x, y, z, roadBlock);
        new ParticleBuilder(ParticleEffect.END_ROD, event.getBlock().getLocation().clone().add(0.5, 0.5, 0.5)).setAmount(5).setOffset(0.2f, 0.2f, 0.2f).setSpeed(0.2f).display(player);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
            //event.setCancelled(true);
            return;
        }
        if (event.getClickedBlock() == null){
            //event.setCancelled(true);
            return;
        }
        if (!event.getPlayer().isSneaking()) return;
        if (!event.getPlayer().equals(player)){
            event.setCancelled(true);
            return;
        }

        if (!isInCuboid(event.getClickedBlock())){
            if(isInStairs(event.getClickedBlock())){
                int x = event.getClickedBlock().getX() - block1.getX();
                RoadBlock roadBlock = road.stairBlock[x];
                if(roadBlock == null) return;
                (new RoadBlockGui(roadBlock, x, 0, 0, this, true)).show(event.getPlayer());
            }
            event.setCancelled(true);
            return;
        }

        int x = event.getClickedBlock().getX() - block1.getX();
        int z = event.getClickedBlock().getZ() - block1.getZ();
        int y = event.getClickedBlock().getY() - block1.getY();
        RoadBlock roadBlock = road.getRoadBlock(x, y, z);
        (new RoadBlockGui(roadBlock, x, y, z, this, false)).show(event.getPlayer());
        event.setCancelled(true);
    }

    public void expand(double yaw, double pitch) {
        boolean remake = false;
        int dx = 0;
        int dy = 0;
        int dz = 0;
        if (pitch >= 60.0) {
            this.road.resize(road.x, road.y + 1, road.z);
            dy = -1;
            remake = true;
        } else if (pitch <= -60.0) {
            this.road.resize(road.x, road.y + 1, road.z);
            remake = true;
        } else if (yaw > -45.0 && yaw <= 45.0) {
            this.road.resize(road.x, road.y, road.z + 1);
            remake = true;
        } else if (yaw > 45.0 && yaw <= 135.0) {
            this.road.resize(road.x + 1, road.y, road.z);
            dx = -1;
            remake = true;
        } else if (yaw > 135.0 || yaw <= -135.0) {
            this.road.resize(road.x, road.y, road.z + 1);
            dz = -1;
            remake = true;
        } else if (yaw > -135.0 && yaw <= -45.0) {
            this.road.resize(road.x + 1, road.y, road.z);
            remake = true;
        }
        if (remake) {
            RoadManager.removeRoadEditor(player);
            RoadManager.createRoadEditor(player, this.road.name, block1.getRelative(dx, dy, dz));
        }

    }

    public void retract(double yaw, double pitch) {
        boolean remake = false;
        int dx = 0;
        int dy = 0;
        int dz = 0;
        if (pitch >= 60.0 && road.y > 1) {
            this.road.resize(road.x, road.y - 1, road.z);
            dy = -1;
            remake = true;
        } else if (pitch <= -60.0 && road.y > 1) {
            this.road.resize(road.x, road.y - 1, road.z);
            remake = true;
        } else if (yaw > -45.0 && yaw <= 45.0 && road.z > 1) {
            this.road.resize(road.x, road.y, road.z - 1);
            remake = true;
        } else if (yaw > 45.0 && yaw <= 135.0 && road.x > 1) {
            this.road.resize(road.x - 1, road.y, road.z);
            dx = -1;
            remake = true;
        } else if (yaw > 135.0 || yaw <= -135.0 && road.z > 1) {
            this.road.resize(road.x, road.y, road.z - 1);
            dz = -1;
            remake = true;
        } else if (yaw > -135.0 && yaw <= -45.0 && road.x > 1) {
            this.road.resize(road.x - 1, road.y, road.z);
            remake = true;
        }
        if (remake) {
            RoadManager.removeRoadEditor(player);
            RoadManager.createRoadEditor(player, this.road.name, block1.getRelative(dx, dy, dz));
        }
    }

    public void setAxis(int x, int y) {
        System.out.println(x+" | "+y);
        if (x < 0 || x > road.x - 1 || y < 0 || y > road.y - 1) return;
        boolean remake = x != road.xAxis || y != road.yAxis;
        System.out.println(remake);
        if (remake) {
            this.road.xAxis = x;
            this.road.yAxis = y;
            RoadManager.removeRoadEditor(player);
            RoadManager.createRoadEditor(player, this.road.name, block1);
        }
    }


    public void removeEditor() {
        this.particleTask.cancel();
        this.stairsTask.cancel();
        unsetBlocksBeneath();
        unsetBlocks();
    }

    private boolean isInStairs(Block block){
        if (!block.getWorld().equals(block1.getWorld())) return false;

        int xMin = Math.min(block1.getX(), block2.getX());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMin = block2.getZ()+1;

        int xMax = Math.max(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());

        return (block.getX() <= xMax && block.getX() >= xMin) &&
                (block.getY() <= yMax && block.getY() >= yMin) &&
                (block.getZ() <= zMin && block.getZ() >= zMin);
    }

    private boolean isInCuboid(Block block) {
        if (!block.getWorld().equals(block1.getWorld())) return false;

        int xMin = Math.min(block1.getX(), block2.getX());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMin = Math.min(block1.getZ(), block2.getZ());

        int xMax = Math.max(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());

        return (block.getX() <= xMax && block.getX() >= xMin) &&
                (block.getY() <= yMax && block.getY() >= yMin) &&
                (block.getZ() <= zMax && block.getZ() >= zMin);
    }

    private void createTask() {
        if (this.particleTask != null && !particleTask.isCancelled()) particleTask.cancel();
        List<Location> cuboidLocations = ParticleUtil.getCuboidLocations(block1, block2);

        int x = road.xAxis;
        int y = road.yAxis;
        Block b1 = block1.getRelative(x, y, 0);
        Block b2 = block1.getRelative(x, y, road.z - 1);

        List<Location> axisLocations = ParticleUtil.getCuboidLocations(b1, b2);

        Location l1 = b1.getLocation().add(0.5, 1.1, 0);
        Location l2 = b2.getLocation().add(0.5, 1.1, 1.0);
        List<Location> arrowLocations = ParticleUtil.getArrowLocations(l1, l2, null, Math.PI / 4.0, 1.0, 8);

        this.particleTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                for (Location location : cuboidLocations) {
                    new ParticleBuilder(ParticleEffect.FLAME, location).setOffset(0f, 0f, 0f).setSpeed(0f).setAmount(3).display(player);
                }
                for (Location location : axisLocations) {
                    new ParticleBuilder(ParticleEffect.CRIT, location).setOffset(0f, 0f, 0f).setSpeed(0f).setAmount(3).display(player);
                }
                for (Location location : arrowLocations) {
                    new ParticleBuilder(ParticleEffect.NAUTILUS, location).setOffset(0f, 0f, 0f).setSpeed(0f).setAmount(3).display(player);
                }
            }
        }.runTaskTimerAsynchronously(Trails.getInstance(), 0L, 10L);


        if (!road.buildStairs) return;
        List<Location> stairs = ParticleUtil.getCuboidLocations(block1.getRelative(0, 0, road.z), block2.getRelative(0, 0, 1));
        this.stairsTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (player == null || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                for (Location location : stairs) {
                    new ParticleBuilder(ParticleEffect.SNEEZE, location).setOffset(0f, 0f, 0f).setSpeed(0f).setAmount(3).display(player);
                }
            }
        }.runTaskTimerAsynchronously(Trails.getInstance(), 0L, 10L);
    }

    private void setupEditor() {
        setBlocksBeneath();

        // setup blueprint body
        for (int x = 0; x < road.x; x++) {
            for (int y = 0; y < road.y; y++) {
                for (int z = 0; z < road.z; z++) {
                    Block block = block1.getRelative(x, y, z);
                    RoadBlock roadBlock = road.getRoadBlock(x, y, z);
                    if (roadBlock == null) {
                        System.out.println("RoadBlock is null");
                        continue;
                    }
                    RoadBlockType roadBlockType = roadBlock.getMaterial();

                    BlockData data = block.getBlockData();
                    replaceBlocks.put(block, new Pair<>(block.getType(), data));

                    block.setType(roadBlockType.material);
                    if (!roadBlockType.blockDataString.equals(""))
                        block.setBlockData(Bukkit.createBlockData(roadBlockType.blockDataString));
                }
            }
        }

        //setup stairs section
        if (!road.buildStairs) return;
        for (int x = 0; x < road.x; x++) {
            for(int y=0;y<road.y;y++){
                Block block = block1.getRelative(x, y, road.z);
                BlockData data = block.getBlockData();
                replaceBlocks.put(block, new Pair<>(block.getType(), data));
            }
            RoadBlock roadBlock = road.stairBlock[x];
            int y = road.stairsY[x];
            if (roadBlock != null && roadBlock.getNonAirType() != null) {
                Block block = block1.getRelative(x, y, road.z);
                RoadBlockType roadBlockType = roadBlock.getNonAirType();
                BlockData data = block.getBlockData();
                replaceBlocks.put(block, new Pair<>(block.getType(), data));
                block.setType(roadBlockType.material);
                if (!roadBlockType.blockDataString.equals(""))
                    block.setBlockData(Bukkit.createBlockData(roadBlockType.blockDataString));
            }
        }

    }

    private void setBlocksBeneath() {
        int xMin = Math.min(block1.getX(), block2.getX());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMin = Math.min(block1.getZ(), block2.getZ());

        int xMax = Math.max(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());
        if (road.buildStairs) zMax++;

        int y = yMin - 1;

        for (int x = xMin; x <= xMax; x++) {
            for (int z = zMin; z <= zMax; z++) {
                Block block = block1.getWorld().getBlockAt(x, y, z);
                if (block.getType() == Material.AIR || block.getType() == Material.SNOW) {
                    block.setType(Material.GLASS);
                    supportBlocks.add(block);
                }
            }
        }
    }

    private void unsetBlocksBeneath() {
        for (Block block : supportBlocks) block.setType(Material.AIR);
    }

    private void unsetBlocks() {
        for (Block block : replaceBlocks.keySet()) {
            Material material = replaceBlocks.get(block).getKey();
            BlockData data = replaceBlocks.get(block).getValue();
            block.setType(material);
            block.setBlockData(data);
        }
    }
}
