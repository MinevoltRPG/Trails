package me.ccrama.Trails.roads;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Road {
    String name;
    RoadBlock[][][] roadBlocks;
    int x;
    int y;
    int z;

    public Road(String name, int x, int y, int z) {
        this.name = name;
        roadBlocks = new RoadBlock[x][y][z];
        this.x = x;
        this.y=y;
        this.z=z;
    }

    @Nullable
    public RoadBlock getRoadBlock(int x, int y, int z){
        if(x<this.x && y<this.y && z<this.z){
            return roadBlocks[x][y][z];
        } else return null;
    }


}
