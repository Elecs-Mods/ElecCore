package elec332.core.api.world;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.chunk.IChunk;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IChunkIOHook {

    void chunkLoadedFromDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager);

    void chunkSavedToDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager);

}
