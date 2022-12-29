package me.ccrama.Trails.util;

import com.jeff_media.customblockdata.CustomBlockData;
import me.ccrama.Trails.Trails;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadUtils {

    static Map<Player, List<Block>> previousRoadBlocks = new HashMap<>();
    static Map<Player, Block> prevCenterBlock = new HashMap<>();
    static Map<Player, Direction> prevDirection = new HashMap<>();

    public static void placeRoad(Player player, Location loc, Direction direction, Road road){
        Block pblock = prevCenterBlock.get(player);
        if(pblock != null){
            int dx = loc.getBlockX()-pblock.getX();
            int dz = loc.getBlockZ()-pblock.getZ();
            //System.out.println(dx+" | "+dz+" | "+Math.abs(dx-dz)+ " | "+direction+" | "+ prevDirection.get(player));
            if(Math.abs(dx-dz) < 0.01) direction = prevDirection.get(player);
        }
        prevCenterBlock.put(player, loc.getBlock());
        prevDirection.put(player, direction);

        List<Block> blocks = new ArrayList<>();
        List<Block> prev = previousRoadBlocks.get(player);
        if(prev == null) prev = new ArrayList<>();
        if(direction == Direction.SOUTH || direction == Direction.NORTH){


            loc.add(-1,0,0);
            CustomBlockData customBlockData;
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }

            loc.add(1,0,0);
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }

            loc.add(1,0,0);
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }

            Location clone = loc.clone();
            if(direction == Direction.SOUTH){
                if(prev.contains(loc.add(0, 1, 1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(-1, 0, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(-1, 0, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }

                if(prev.contains(clone.add(0, -1, 1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(-1, 0, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(-1, 0, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,-1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
            } else {
                System.out.println("NORTh");
                if(prev.contains(loc.add(0, 1, -1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(-1, 0, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(-1, 0, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(0,0,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }

                if(prev.contains(clone.add(0, -1, -1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(-1, 0, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(-1, 0, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(0,1,1).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
            }
        } else{
            loc.add(0,0,-1);
            CustomBlockData customBlockData;

            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }

            loc.add(0,0,1);
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }

            loc.add(0,0,1);
            if(loc.getBlock().getType() != Material.AIR) {
                loc.getBlock().setType(Material.COBBLESTONE);
                customBlockData = new CustomBlockData(loc.getBlock(), Trails.getInstance());
                customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                blocks.add(loc.getBlock());
            }


            Location clone = loc.clone();
            if(direction == Direction.EAST){
                if(prev.contains(loc.add(1, 1, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(-1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(0, 0, -1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(-1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(0, 0, -1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(-1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }

                if(prev.contains(clone.add(1, -1, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(-1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(0, 0, -1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(-1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(0, 0, -1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(-1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
            } else {
                if(prev.contains(loc.add(-1, 1, 0).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(0, 0, -1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(loc.add(0, 0, -1).getBlock())){
                    Location tmp = loc.clone();
                    tmp.add(1,0,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.NORTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }

                if(prev.contains(clone.add(-1, -1, 0).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(0, 0, -1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
                if(prev.contains(clone.add(0, 0, -1).getBlock())){
                    Location tmp = clone.clone();
                    tmp.add(1,1,0).getBlock().setType(Material.COBBLESTONE_STAIRS);
                    Stairs stairs = (Stairs) tmp.getBlock().getBlockData();
                    stairs.setFacing(BlockFace.SOUTH);
                    tmp.getBlock().setBlockData(stairs);
                    customBlockData = new CustomBlockData(tmp.getBlock(), Trails.getInstance());
                    customBlockData.set(new NamespacedKey(Trails.getInstance(), "road"), PersistentDataType.STRING, "yes");
                }
            }

        }
        previousRoadBlocks.put(player, blocks);
    }

    public enum Direction{
        SOUTH,
        NORTH,
        WEST,
        EAST
    }

}
