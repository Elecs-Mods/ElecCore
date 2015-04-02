package elec332.core.util;

import net.minecraft.block.Block;

/**
 * Created by Elec332 on 8-2-2015.
 */
public class WorldGenInfo {
    int clusterSize = 0;

    int multiplier = 1;
    boolean shouldGen = true;
    public int timesPerChunk;
    public int yLevelMax;
    public Block block;
    public WorldGenInfo(int yLevelMax, int timesPerChunk, Block block){
        this.yLevelMax = yLevelMax;
        this.timesPerChunk = timesPerChunk;
        this.block = block;
    }


    public int getClusterSize(){
        return clusterSize;
    }

    //Gets worldGen info
    public int getGenerationMultiplier(){
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
    public WorldGenInfo setGenerationMultiplier(int i){
        this.multiplier = i;
        return this;
    }

    public WorldGenInfo setShouldGen(boolean b){
        this.shouldGen = b;
        return this;
    }
}
