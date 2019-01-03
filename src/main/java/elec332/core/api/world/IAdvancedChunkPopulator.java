package elec332.core.api.world;

import net.minecraft.world.IWorld;
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

    public boolean populateChunk(IChunkGenerator chunkGenerator, IWorld world, Random rand, int chunkX, int chunkZ);

    public boolean retroGen(Random random, int chunkX, int chunkZ, IWorld world);

}
