package me.ccrama.Trails.roads;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoadEditor implements Listener {
    Player player;
    Block block1;
    Block block2;
    Road road;

    List<Block> supportBlocks = new ArrayList<>();

    public RoadEditor(Player player, Block block1, Block block2, Road road) {
        this.player = player;
        this.block1 = block1;
        this.block2 = block2;
        this.road = road;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event){

    }

    private void setBlocksBeneath(){
        int xMin = Math.min(block1.getX(), block2.getX());
        int yMin = Math.min(block1.getY(), block2.getY());
        int zMin = Math.min(block1.getZ(), block2.getZ());

        int xMax = Math.max(block1.getX(), block2.getX());
        int yMax = Math.max(block1.getY(), block2.getY());
        int zMax = Math.max(block1.getZ(), block2.getZ());

        int y = yMin-1;

        for(int x = xMin; x<= xMax;x++){
            for(int z = zMin; z<=zMax;z++){
                Block block = block1.getWorld().getBlockAt(x,y,z);
                if(block.getType() == Material.AIR){
                    block.setType(Material.GLASS);
                    supportBlocks.add(block);
                }
            }
        }
    }

    private void unsetBlocksBeneath(){
        for(Block block : supportBlocks) block.setType(Material.AIR);
    }
}
