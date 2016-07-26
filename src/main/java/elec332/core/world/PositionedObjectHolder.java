package elec332.core.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 4-2-2016.
 */
public class PositionedObjectHolder<T> {

    public PositionedObjectHolder(){
        this(256);
    }

    @SuppressWarnings("unused")
    private PositionedObjectHolder(int height){
        positionedMap = new Long2ObjectLinkedOpenHashMap<PositionChunk>();
    }

    private final Long2ObjectMap<PositionChunk> positionedMap;

    @Nullable
    public T get(BlockPos pos){
        return getChunkForPos(pos).get(pos);
    }

    @Nonnull
    private PositionChunk getChunkForPos(BlockPos pos){
        return getChunkForPos(new ChunkPos(pos));
    }

    @Nonnull
    private PositionChunk getChunkForPos(ChunkPos chunkPos){
        long l = WorldHelper.longFromChunkXZ(chunkPos);
        PositionChunk positionChunk = positionedMap.get(l);
        if (positionChunk == null){
            positionChunk = new PositionChunk(chunkPos);
            positionedMap.put(l, positionChunk);
        }
        return positionChunk;
    }

    public void put(T t, BlockPos pos){
        getChunkForPos(pos).put(t, pos);
    }

    public void remove(BlockPos pos){
        getChunkForPos(pos).remove(pos);
    }

    public Set<ChunkPos> getChunks(){
        Set<ChunkPos> ret = Sets.newHashSet();
        for (PositionChunk chunk : positionedMap.values()){
            ret.add(chunk.pos);
        }
        return ret;
    }

    @Nonnull
    public Map<BlockPos, T> getObjectsInChunk(ChunkPos chunk){
        return getChunkForPos(chunk).publicVisibleMap;
    }

    public boolean chunkExists(ChunkPos chunk){
        return positionedMap.containsKey(WorldHelper.longFromChunkXZ(chunk));
    }

    public boolean hasObject(BlockPos pos){
        long l = WorldHelper.longFromBlockPos(pos);
        return positionedMap.containsKey(l) && getChunkForPos(new ChunkPos(pos)).get(pos) != null;
    }

    /**
     * Just like MC chunks, store the stuff in smaller patches.
     */
    private class PositionChunk {

        private PositionChunk(ChunkPos pos){
            this.pos = pos;
            this.posMap = Maps.newHashMap();
            this.publicVisibleMap = Collections.unmodifiableMap(this.posMap);
        }

        private final ChunkPos pos;
        private final Map<BlockPos, T> posMap;
        private final Map<BlockPos, T> publicVisibleMap;

        private T get(BlockPos pos){
            return posMap.get(pos);
        }

        private void put(T t, BlockPos pos){
            posMap.put(pos, t);
        }

        private void remove(BlockPos pos){
            posMap.remove(pos);
        }

    }

}
