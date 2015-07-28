package elec332.core.world;

import cpw.mods.fml.common.IWorldGenerator;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.util.WorldGenInfo;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.config.Configuration;

import java.io.File;
import java.util.Random;

/**
 * Created by Elec332 on 2-4-2015.
 */
public abstract class WorldGen implements IWorldGenerator {

    public WorldGen(File configFile){
        this.configuration = new Configuration(configFile);
    }

    private Configuration configuration;

    public WorldGenInfo configurableWorldGen(String oreName, int clusterSize, int maxY, int timesPerChunk, Block block){
        return configurableWorldGen(oreName, clusterSize, maxY, timesPerChunk, block, 0);
    }

    public WorldGenInfo configurableWorldGen(String oreName, int clusterSize, int maxY, int timesPerChunk, Block block, int meta){
        configuration.load();
        boolean s = configuration.getBoolean("Should_gen", oreName, true, "Sets if the ore should generate in the world or not");
        int m = configuration.getInt("Generation_multiplier", oreName, 100, 0, 1000, "Sets how many times the mod will attempt to generate ores per chunk (In % from the default value)");
        int c = configuration.getInt("ClusterSize", oreName, clusterSize, 0, 30, "Sets the max cluster size for this ore");
        configuration.save();
        return new WorldGenInfo(maxY, timesPerChunk, block, meta).setGenerationMultiplier(((float)m)/100).setShouldGen(s).setClusterSize(c);
    }

    public void register(){
        GameRegistry.registerWorldGenerator(this, 1000);
    }

    public void generateEnd(World world, Random random, int chunkX, int chunkZ, WorldGenInfo info) {
        if (info.getShouldGen()){
            for (int i = 0; i < info.timesPerChunk * info.getGenerationMultiplier(); i++) {
                int xCoord = chunkX*16 + random.nextInt(16);
                int yCoord = random.nextInt(info.yLevelMax); //Max Y value, should we make this configurable aswell?
                int zCoord = chunkZ*16 + random.nextInt(16);
                (new WorldGenMinable(info.block, info.meta, info.getClusterSize(), Blocks.end_stone)).generate(world, random, xCoord, yCoord, zCoord);
            }
        }
    }

    public void generateNether(World world, Random random, int chunkX, int chunkZ, WorldGenInfo info) {
        if (info.getShouldGen()){
            for (int i = 0; i < info.timesPerChunk * info.getGenerationMultiplier(); i++) {
                int xCoord = chunkX*16 + random.nextInt(16);
                int yCoord = random.nextInt(info.yLevelMax); //Max Y value, should we make this configurable aswell?
                int zCoord = chunkZ*16 + random.nextInt(16);
                (new WorldGenMinable(info.block, info.meta, info.getClusterSize(), Blocks.netherrack)).generate(world, random, xCoord, yCoord, zCoord);
            }
        }
    }

    public void generateOverworld(World world, Random random, int chunkX, int chunkZ, WorldGenInfo info) {
        if (info.getShouldGen()){
            for (int i = 0; i < info.timesPerChunk * info.getGenerationMultiplier(); i++) {
                int xCoord = chunkX*16 + random.nextInt(16);
                int yCoord = random.nextInt(info.yLevelMax); //Max Y value, should we make this configurable aswell?
                int zCoord = chunkZ*16 + random.nextInt(16);
                (new WorldGenMinable(info.block, info.meta, info.getClusterSize(), Blocks.stone)).generate(world, random, xCoord, yCoord, zCoord);
            }
        }
    }
}
