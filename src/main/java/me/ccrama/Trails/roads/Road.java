package me.ccrama.Trails.roads;

import org.bukkit.Material;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class Road {
    public String name;
    public RoadBlock[][][] roadBlocks;
    public int x;
    public int y;
    public int z;
    public int yAxis;
    public int xAxis;
    public int maxYgrad = 1;
    public int[] stairsY;
    public RoadBlock[] stairBlock;
    public boolean buildStairs = true;
    public int forward = 1;

    public Road(String name, int x, int y, int z, int xAxis, int yAxis, int stairsY) {
        this.name = name;
        roadBlocks = new RoadBlock[x][y][z];
        this.x = x;
        this.y=y;
        this.z=z;
        this.xAxis=xAxis;
        this.yAxis=yAxis;
        this.stairsY=new int[x];
        for(int i=0;i<x;i++) this.stairsY[i] = stairsY;
        stairBlock = new RoadBlock[x];
        fillDefaultStairs();
        fillEmpty();
    }

    private void fillDefaultStairs(){
        for(int i=0;i<x;i++){
            if(stairBlock[i] != null) continue;
            RoadBlock roadBlock = new RoadBlock();
            RoadBlockType roadBlockType = new RoadBlockType(Material.OAK_STAIRS, 1.0, null, null, "");
            roadBlock.addMaterial(roadBlockType);
            stairBlock[i] = roadBlock;
        }
    }

    private void fillEmpty(){
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                for(int k=0;k<z;k++){
                    if(roadBlocks[i][j][k] == null) roadBlocks[i][j][k] = new RoadBlock();
                }
            }
        }
    }

    public void resize(int x, int y, int z){
        RoadBlock[][][] tmp = new RoadBlock[x][y][z];
        int xMin = Math.min(x, this.x);
        int yMin = Math.min(y, this.y);
        int zMin = Math.min(z, this.z);

        for(int i=0;i<xMin;i++){
            for (int j=0;j<yMin;j++){
                if (zMin >= 0) System.arraycopy(this.roadBlocks[i][j], 0, tmp[i][j], 0, zMin);
            }
        }
        this.roadBlocks=tmp;
        this.x = x;
        this.y=y;
        this.z=z;
        RoadBlock[] tmp2 = new RoadBlock[x];
        if (xMin >= 0) System.arraycopy(stairBlock, 0, tmp2, 0, xMin);
        this.stairBlock=tmp2;

        fillEmpty();
        fillDefaultStairs();

    }

    @Nullable
    public RoadBlock getRoadBlock(int x, int y, int z){
        if(x<this.x && y<this.y && z<this.z){
            return roadBlocks[x][y][z];
        } else return null;
    }

    public void setRoadBlock(int x, int y, int z, RoadBlock roadBlock){
        if(x >= this.x || y>=this.y || z>=this.z) return;
        roadBlocks[x][y][z] = roadBlock;
    }


}
