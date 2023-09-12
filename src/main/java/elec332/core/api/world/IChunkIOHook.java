package elec332.core.api.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.chunk.IChunk;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IChunkIOHook {

    public void chunkLoadedFromDisk(IChunk chunk, CompoundTag data, IWorldGenManager worldGenManager);

    public void chunkSavedToDisk(IChunk chunk, CompoundTag data, IWorldGenManager worldGenManager);

}
