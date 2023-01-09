package me.ccrama.Trails.roads;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;

public class RoadBlockType {
    public Material material;

    double weight;
    public String trailName = null;
    public Integer trailWalks = null;
    public String blockDataString;
    public boolean replaceAir = true;


    public RoadBlockType(Material material, double weight, String trailName, Integer trailWalks, String blockDataString) {
        this.material = material;
        this.weight = weight;
        this.trailName = trailName;
        this.trailWalks = trailWalks;
        this.blockDataString = blockDataString;
    }
}
