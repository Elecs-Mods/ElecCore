package elec332.core.world;

import com.google.common.collect.Maps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;

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
        return getChunkForPos(pos).get(pos);
    }

    private PositionChunk getChunkForPos(BlockPos pos){
        long value = ChunkCoordIntPair.chunkXZ2Int(pos.getX(), pos.getZ());
        PositionChunk positionChunk = positionedMap.getValueByKey(value);
        if (positionChunk == null){
            positionChunk = new PositionChunk();
            positionedMap.add(value, positionChunk);
        }
        return positionChunk;
    }

    public void put(T t, BlockPos pos){
        getChunkForPos(pos).put(t, pos);
    }

    public void remove(BlockPos pos){
        getChunkForPos(pos).remove(pos);
    }

    /**
     * Just like MC chunks, store the stuff in smaller patches.
     */
    private class PositionChunk {

        private PositionChunk(){
            this.posMap = Maps.newHashMap();
        }

        private Map<BlockPos, T> posMap;

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
