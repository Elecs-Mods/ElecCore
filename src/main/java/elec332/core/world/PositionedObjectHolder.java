package elec332.core.world;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Collections;
import java.util.Map;

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

    private Long2ObjectMap<PositionChunk> positionedMap;

    public T get(BlockPos pos){
        return getChunkForPos(WorldHelper.longFromBlockPos(pos)).get(pos);
    }

    private PositionChunk getChunkForPos(long chunkPos){
        PositionChunk positionChunk = positionedMap.get(chunkPos);
        if (positionChunk == null){
            positionChunk = new PositionChunk();
            positionedMap.put(chunkPos, positionChunk);
        }
        return positionChunk;
    }

    public void put(T t, BlockPos pos){
        getChunkForPos(WorldHelper.longFromBlockPos(pos)).put(t, pos);
    }

    public void remove(BlockPos pos){
        getChunkForPos(WorldHelper.longFromBlockPos(pos)).remove(pos);
    }

    public Map<BlockPos, T> getObjectsInChunk(ChunkPos chunk){
        return getChunkForPos(WorldHelper.longFromChunkXZ(chunk)).publicVisibleMap;
    }

    public boolean chunkExists(ChunkPos chunk){
        return positionedMap.containsKey(WorldHelper.longFromChunkXZ(chunk));
    }

    /**
     * Just like MC chunks, store the stuff in smaller patches.
     */
    private class PositionChunk {

        private PositionChunk(){
            this.posMap = Maps.newHashMap();
            this.publicVisibleMap = Collections.unmodifiableMap(this.posMap);
        }

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
