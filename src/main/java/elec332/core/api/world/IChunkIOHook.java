package elec332.core.api.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.IChunk;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IChunkIOHook {

    public void chunkLoadedFromDisk(IChunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

    public void chunkSavedToDisk(IChunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager);

}
