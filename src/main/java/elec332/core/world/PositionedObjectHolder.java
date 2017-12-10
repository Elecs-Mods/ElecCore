package elec332.core.world;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
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
        this(new Long2ObjectLinkedOpenHashMap<PositionChunk>());
    }

    private PositionedObjectHolder(Map<Long, PositionChunk> positionedMap){
        this.positionedMap = positionedMap;
        this.callbacks = Sets.newHashSet();
        this.hasCallbacks = false;
    }

    private final Map<Long, PositionChunk> positionedMap;
    private final Set<ChangeCallback<T>> callbacks;
    private boolean hasCallbacks;

    public void clear(){
        positionedMap.values().forEach(PositionChunk::clear);
        positionedMap.clear();
        callbacks.clear();
    }

    public void registerCallback(ChangeCallback<T> callback){
        if (callback == null){
            return;
        }
        callbacks.add(callback);
        if (!hasCallbacks){
            hasCallbacks = true;
        }
    }

    @Nullable
    public T get(BlockPos pos){
        return getChunkForPos(pos).get(pos);
    }

    @Nonnull
    private PositionChunk getChunkForPos(BlockPos pos){
        return getChunkForPos(new ChunkPos(pos));
    }

    @Nonnull
    @SuppressWarnings("all")
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
        PositionChunk chunk = getChunkForPos(pos);
        chunk.remove(pos);
        if (chunk.posMap.isEmpty()){
            positionedMap.remove(WorldHelper.longFromChunkXZ(chunk.pos));
        }
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
            if (hasCallbacks){
                for (ChangeCallback<T> callback : callbacks){
                    callback.onChange(t, pos, true);
                }
            }
        }

        private void remove(BlockPos pos){
            T t = posMap.remove(pos);
            if (hasCallbacks){
                for (ChangeCallback<T> callback : callbacks){
                    callback.onChange(t, pos, false);
                }
            }
        }

        public void clear(){
            posMap.clear();
        }

    }

    public interface ChangeCallback<T> {

        public void onChange(T object, BlockPos pos, boolean add);

    }

    public static <T> PositionedObjectHolder<T> immutableCopy(PositionedObjectHolder<T> original){

        return new PositionedObjectHolder<T>(original.positionedMap){

            @Override
            public void put(T t, BlockPos pos) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove(BlockPos pos) {
                throw new UnsupportedOperationException();
            }

        };

    }

}
