package elec332.core.api.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.IChunk;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IChunkIOHook {

    public void chunkLoadedFromDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager);

    public void chunkSavedToDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager);

}
