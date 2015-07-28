package elec332.core.util;

import net.minecraft.block.Block;

/**
 * Created by Elec332 on 8-2-2015.
 */
public class WorldGenInfo {
    int clusterSize = 0;

    float multiplier = 1;
    boolean shouldGen = true;
    public int timesPerChunk;
    public int yLevelMax;
    public final Block block;
    public final int meta;

    public WorldGenInfo(int yLevelMax, int timesPerChunk, Block block, int meta){
        this.yLevelMax = yLevelMax;
        this.timesPerChunk = timesPerChunk;
        this.block = block;
        this.meta = meta;
    }

    public WorldGenInfo(int yLevelMax, int timesPerChunk, Block block){
        this.yLevelMax = yLevelMax;
        this.timesPerChunk = timesPerChunk;
        this.block = block;
        this.meta = 0;
    }


    public int getClusterSize(){
        return clusterSize;
    }

    //Gets worldGen info
    public float getGenerationMultiplier(){
        return multiplier;
    }

    public Boolean getShouldGen(){
        return shouldGen;
    }

    public WorldGenInfo setClusterSize(int i){
        this.clusterSize = i;
        return this;
    }

    //Sets worldGen info
    public WorldGenInfo setGenerationMultiplier(float i){
        this.multiplier = i;
        return this;
    }

    public WorldGenInfo setShouldGen(boolean b){
        this.shouldGen = b;
        return this;
    }
}
