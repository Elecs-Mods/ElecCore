package elec332.core.api.world;

import net.minecraft.world.World;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Random;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IAdvancedChunkPopulator {

    public String getName();

    public String getGenKey();

    default public boolean shouldRegen(boolean hasInitialGen) {
        return true;
    }

    public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated);

    public boolean retroGen(Random random, int chunkX, int chunkZ, World world);

}
