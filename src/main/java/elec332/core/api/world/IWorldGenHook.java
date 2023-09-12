package elec332.core.api.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenHook extends IChunkIOHook {

    public boolean populateChunk(ChunkGenerator<? extends GenerationSettings> chunkGenerator, IWorld world, Random rand, int chunkX, int chunkZ);

    @Override
    public void chunkLoadedFromDisk(IChunk chunk, CompoundTag data, IWorldGenManager worldGenManager);

    @Override
    public void chunkSavedToDisk(IChunk chunk, CompoundTag data, IWorldGenManager worldGenManager);

}
