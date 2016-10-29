package elec332.core.api.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenHook {

    public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated);

    public void chunkLoadedFromDisk(Chunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

    public void chunkSavedToDisk(Chunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

}
