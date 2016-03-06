package elec332.core.world;

import com.google.common.collect.Maps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;

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
        positionedMap = new LongHashMap<PositionChunk>();
    }

    private LongHashMap<PositionChunk> positionedMap;

    public T get(BlockPos pos){
        return getChunkForPos(WorldHelper.longFromBlockPos(pos)).get(pos);
    }

    private PositionChunk getChunkForPos(long chunkPos){
        PositionChunk positionChunk = positionedMap.getValueByKey(chunkPos);
        if (positionChunk == null){
            positionChunk = new PositionChunk();
            positionedMap.add(chunkPos, positionChunk);
        }
        return positionChunk;
    }

    public void put(T t, BlockPos pos){
        getChunkForPos(WorldHelper.longFromBlockPos(pos)).put(t, pos);
    }

    public void remove(BlockPos pos){
        getChunkForPos(WorldHelper.longFromBlockPos(pos)).remove(pos);
    }

    public Map<BlockPos, T> getObjectsInChunk(ChunkCoordIntPair chunk){
        return getChunkForPos(WorldHelper.longFromChunkXZ(chunk)).publicVisibleMap;
    }

    public boolean chunkExists(ChunkCoordIntPair chunk){
        return positionedMap.containsItem(WorldHelper.longFromChunkXZ(chunk));
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
