package elec332.core.api.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.IWorld;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenHook extends IChunkIOHook {

    public boolean populateChunk(IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, IWorld world, Random rand, int chunkX, int chunkZ);

    @Override
    public void chunkLoadedFromDisk(IChunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

    @Override
    public void chunkSavedToDisk(IChunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

}
