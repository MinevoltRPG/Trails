package me.ccrama.Trails.util;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import me.ccrama.Trails.roads.Road;
import me.ccrama.Trails.roads.RoadBlock;
import me.ccrama.Trails.roads.RoadBlockType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class RoadUtils {

    static Map<Player, Integer> zSlices = new HashMap<>();
    static Map<Player, RoadHistory> history = new HashMap<>();
    static Map<Player, List<Pair<Integer, Block>>> placesForStairs = new HashMap<>();

    public static void placeRoad(Player player, Location loc, Direction direction, Road road) {
        placeSection(loc.getBlock(), player, road, direction);
    }

    public static void placeSection(Block block, Player player, Road road, Direction direction) {
        RoadHistory roadHistory = history.get(player);
        Integer yLevel = null;
        if(roadHistory != null){
            int x = roadHistory.x;
            int z = roadHistory.z;
            int dx = block.getX() - x;
            int dz = block.getZ()-z;

            if(Math.abs(dx) + Math.abs(dz) > 6){
                yLevel = block.getY();
                history.remove(player);
                System.out.println("removing player history");
            }
            else {
                yLevel = roadHistory.y;

                Direction dir = roadHistory.direction;
                if(dir != direction && block.getY() != yLevel){
                    direction = dir;
                    int ddx = 0;
                    int ddz = 0;

                    if(direction == Direction.WEST) ddx = -1;
                    else if(direction == Direction.EAST) ddx = 1;
                    else if(direction == Direction.NORTH) ddz = -1;
                    else if(direction == Direction.SOUTH) ddz = 1;
                    block = block.getWorld().getBlockAt(x+ddx, yLevel, z+ddz);
                } else if(dir != direction){
                    System.out.println(dx+" | "+dz);
                }
            }
        }
        if (yLevel == null) yLevel = block.getY();


        int changedLevel = 0;
        if (yLevel > block.getY()) changedLevel = -1;
        else if (yLevel < block.getY()) changedLevel = 1;

        if (Math.abs(block.getY() - yLevel) > road.maxYgrad) {
            if (block.getY() - yLevel > 0) yLevel = yLevel + Math.abs(road.maxYgrad);
            else yLevel = yLevel - Math.abs(road.maxYgrad);
            block = block.getRelative(0, yLevel - block.getY(), 0);
        } else yLevel = block.getY();

        Integer zSlice = zSlices.get(player);
        if (zSlice == null) zSlice = 0;
        if (zSlice >= road.z) zSlice = 0;
        int xAxis = road.xAxis;
        int yAxis = road.yAxis;


        List<Pair<Integer, Block>> stairsPlaces = new ArrayList<>();
        Set<Block> pastedBlocks = new HashSet<>();
        for (int x = 0; x < road.x; x++) {
            int xVal = x - xAxis;

            int zVal = 0;
            if (direction == Direction.WEST || direction == Direction.EAST) {
                zVal = xVal;
                xVal = 0;
            }

            Block stair = block.getRelative(xVal, road.stairsY[x], zVal);
            stairsPlaces.add(new Pair<>(x, stair));

            for (int y = 0; y < road.y; y++) {
                int yVal = y - yAxis;
                RoadBlock roadBlock = road.getRoadBlock(x, y, zSlice);
                if(roadBlock == null) continue;
                RoadBlockType roadBlockType = roadBlock.getMaterial();

                // Block to replace
                Block rep = block.getRelative(xVal, yVal, zVal);

                if(rep.getType() == Material.AIR){
                    if(!roadBlock.replaceAir || !roadBlockType.replaceAir){
                        System.out.println("Skippng (air)");
                        continue;
                    }
                }

                rep.setType(roadBlockType.material);
                if (!roadBlockType.blockDataString.equals("")) {
                    rep.setBlockData(Bukkit.createBlockData(roadBlockType.blockDataString));
                }

                pastedBlocks.add(rep);

                CustomBlockData customBlockData = new CustomBlockData(rep, Trails.getInstance());
                if (roadBlockType.trailName != null) {
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "n"), PersistentDataType.STRING, roadBlockType.trailName);
                }
                if (roadBlockType.trailWalks != null) {
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "w"), PersistentDataType.INTEGER, roadBlockType.trailWalks);
                }
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "r"), PersistentDataType.STRING, road.name);

            }
        }
        zSlice++;
        zSlices.put(player, zSlice);
        history.put(player, new RoadHistory(yLevel, block.getX(), block.getZ(), direction, pastedBlocks));
        Set<Block> historyBlocks = null;
        if(roadHistory != null)historyBlocks = roadHistory.placed;

        if (changedLevel != 0 && road.buildStairs) placeStairs(player, block, road, direction, changedLevel, historyBlocks);

        placesForStairs.put(player, stairsPlaces);
    }

    public static void placeStairs(Player player, Block block, Road road, Direction direction, int ver,Set<Block> pastedBlocks) {
        List<Pair<Integer, Block>> places = placesForStairs.get(player);
        if (places == null|| pastedBlocks == null) return;

        int dx = 0;
        int dy = ver;
        int dz = 0;

        if (dy < 0) {
            if (direction == Direction.NORTH) dz = -1;
            else if (direction == Direction.SOUTH) dz = 1;
            else if (direction == Direction.EAST) dx = 1;
            else if (direction == Direction.WEST) dx = -1;
            dy = 0;
        }

        for (Pair<Integer, Block> pair : places) {
            Integer x = pair.getKey();
            Block b = pair.getValue().getRelative(dx, dy, dz);
            Direction direction1 = getAdjusted(b, pastedBlocks);
            System.out.println(direction+" | "+direction1);
            if(direction1 == null) continue;

            RoadBlock stairBlock = road.stairBlock[x];
            if(stairBlock == null) continue;
            RoadBlockType roadBlockType = stairBlock.getMaterial();
            b.setType(roadBlockType.material);
            CustomBlockData customBlockData = new CustomBlockData(b, Trails.getInstance());
            customBlockData.set(new NamespacedKey(Trails.getInstance(), "r"), PersistentDataType.STRING, road.name);
            if (!roadBlockType.blockDataString.equals(""))
                b.setBlockData(Bukkit.createBlockData(roadBlockType.blockDataString));

            if (b.getBlockData() instanceof Stairs) {
                //System.out.println(player.getFacing()+" | "+direction);
                Stairs stairs = (Stairs) b.getBlockData();
                if (direction == Direction.NORTH){
                    if(dy>0) stairs.setFacing(BlockFace.NORTH);
                    else stairs.setFacing(BlockFace.SOUTH);
                }
                else if (direction == Direction.SOUTH){
                    if(dy>0)stairs.setFacing(BlockFace.SOUTH);
                    else stairs.setFacing(BlockFace.NORTH);
                }
                else if (direction == Direction.EAST){
                    if(dy>0)stairs.setFacing(BlockFace.EAST);
                    else stairs.setFacing(BlockFace.WEST);
                }
                else if (direction == Direction.WEST){
                    if(dy>0) stairs.setFacing(BlockFace.WEST);
                    else stairs.setFacing(BlockFace.EAST);
                }
                b.setBlockData(stairs);
            }
        }
    }


    public static Direction getAdjusted(Block block, Set<Block> pastedBlocks){
        Block b1 = block.getRelative(1,0,0);
        Block b2 = block.getRelative(-1,0,0);
        Block b3 = block.getRelative(0,0,1);
        Block b4 = block.getRelative(0,0,-1);

        if(pastedBlocks.contains(b1)) return  Direction.WEST;
        if(pastedBlocks.contains(b2)) return Direction.EAST;
        if(pastedBlocks.contains(b3)) return Direction.NORTH;
        if(pastedBlocks.contains(b4)) return Direction.SOUTH;
        return null;
    }



    public static void clearRoadCache(Player player) {
        history.remove(player);
        zSlices.remove(player);
    }

    private static class RoadHistory{

        public RoadHistory(int y, int x, int z, Direction direction, Set<Block> placed) {
            this.y = y;
            this.x = x;
            this.z = z;
            this.direction = direction;
            this.placed = placed;
        }

        Set<Block> placed;
        int y;
        int x;
        int z;
        Direction direction;
    }

    public enum Direction {
        SOUTH,
        NORTH,
        WEST,
        EAST
    }

}
