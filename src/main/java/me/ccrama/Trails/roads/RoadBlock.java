package me.ccrama.Trails.roads;

import org.bukkit.Material;

import java.util.*;

public class RoadBlock {
    List<RoadBlockType> materialWeight = new ArrayList<>();
    double volume=0;
    public boolean replaceAir = true;

    public RoadBlock(List<RoadBlockType> w){
        this.materialWeight = w;
        calcVolume();

    }

    public RoadBlock(){
    }

    private void calcVolume(){
        double total = 0;
        for(RoadBlockType r : materialWeight){
            total+=r.weight;
        }
        this.volume = total;
    }

    public void addMaterial(RoadBlockType r){
        materialWeight.add(r);
        materialWeight.sort((r1, r2) -> (int) (r1.weight-r2.weight));
        calcVolume();
    }

    public RoadBlockType getMaterial(){
        if(volume <= 0.0) return new RoadBlockType(Material.AIR, 1.0, null, null, "");
        double rnd = (new Random()).nextDouble(volume);
        double current = 0;
        for(RoadBlockType r : materialWeight){
            current+=r.weight;
            if(current>= rnd) return r;
        }
        return new RoadBlockType(Material.AIR, 1.0, null, null, "");
    }

    public RoadBlockType getNonAirType(){
        if(volume == 0.0) return null;
        for(RoadBlockType r : materialWeight){
            if(r.material != Material.AIR) return r;
        }
        return null;
    }
}
