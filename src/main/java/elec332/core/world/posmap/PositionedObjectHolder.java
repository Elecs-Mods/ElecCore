package elec332.core.world.posmap;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.util.IClearable;
import elec332.core.world.WorldHelper;
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
 *
 * A fast way to store objects in a 3D coordinate based map
 */
public class PositionedObjectHolder<T> implements IClearable {

    public PositionedObjectHolder() {
        this(256);
    }

    @SuppressWarnings("unused")
    private PositionedObjectHolder(int height) {
        this(new Long2ObjectLinkedOpenHashMap<PositionChunk>());
    }

    private PositionedObjectHolder(Map<Long, PositionChunk> positionedMap) {
        this.positionedMap = positionedMap;
        this.callbacks = Sets.newHashSet();
        this.hasCallbacks = false;
    }

    private final Map<Long, PositionChunk> positionedMap;
    private final Set<ChangeCallback<T>> callbacks;
    private boolean hasCallbacks;

    /**
     * Clears the entire map, including callbacks!
     */
    @Override
    public void clear() {
        positionedMap.values().forEach(PositionChunk::clear);
        positionedMap.clear();
        callbacks.clear();
    }

    /**
     * Registers a callback to this map
     *
     * @param callback The callback to be registered
     */
    public void registerCallback(ChangeCallback<T> callback) {
        if (callback == null) {
            return;
        }
        callbacks.add(callback);
        if (!hasCallbacks) {
            hasCallbacks = true;
        }
    }

    /**
     * Gets the object at the specified coordinates.
     *
     * @param pos The position to check
     * @return The object at the specified coordinates, can be null.
     */
    @Nullable
    public T get(BlockPos pos) {
        return getChunkForPos(pos).get(pos);
    }

    @Nonnull
    private PositionChunk getChunkForPos(BlockPos pos) {
        return getChunkForPos(new ChunkPos(pos));
    }

    @Nonnull
    @SuppressWarnings("all")
    private PositionChunk getChunkForPos(ChunkPos chunkPos) {
        long l = WorldHelper.longFromChunkPos(chunkPos);
        PositionChunk positionChunk = positionedMap.get(l);
        if (positionChunk == null) {
            positionChunk = new PositionChunk(chunkPos);
            positionedMap.put(l, positionChunk);
        }
        return positionChunk;
    }

    /**
     * Puts an object at the specified position
     *
     * @param t The object to be stored
     * @param pos The position
     */
    public void put(T t, BlockPos pos) {
        getChunkForPos(pos).put(t, pos);
    }

    /**
     * Removes the object at the specified coordinated
     *
     * @param pos The position to be cleared
     */
    public void remove(BlockPos pos) {
        PositionChunk chunk = getChunkForPos(pos);
        chunk.remove(pos);
        if (chunk.posMap.isEmpty()) {
            positionedMap.remove(WorldHelper.longFromChunkPos(chunk.pos));
        }
    }

    /**
     * @return Gets all chunk positions that are in this map
     */
    public Set<ChunkPos> getChunks() {
        Set<ChunkPos> ret = Sets.newHashSet();
        for (PositionChunk chunk : positionedMap.values()) {
            ret.add(chunk.pos);
        }
        return ret;
    }

    @Nonnull
    public Map<BlockPos, T> getObjectsInChunk(ChunkPos chunk) {
        return getChunkForPos(chunk).publicVisibleMap;
    }

    /**
     * Whether data about the specified {@link ChunkPos} chunk exists
     *
     * @param chunkPos The chunk position
     * @return Whether data about the specified {@link ChunkPos} exists
     */
    public boolean chunkExists(ChunkPos chunkPos) {
        return positionedMap.containsKey(WorldHelper.longFromChunkPos(chunkPos));
    }

    /**
     * Whether there is an object at the provided position
     *
     * @param pos The position to be checked
     * @return Whether there is an object at the provided position
     */
    public boolean hasObject(BlockPos pos) {
        long l = WorldHelper.chunkLongFromBlockPos(pos);
        return positionedMap.containsKey(l) && getChunkForPos(new ChunkPos(pos)).get(pos) != null;
    }

    /**
     * Just like MC chunks, store the stuff in smaller patches.
     */
    private class PositionChunk {

        private PositionChunk(ChunkPos pos) {
            this.pos = pos;
            this.posMap = Maps.newHashMap();
            this.publicVisibleMap = Collections.unmodifiableMap(this.posMap);
        }

        private final ChunkPos pos;
        private final Map<BlockPos, T> posMap;
        private final Map<BlockPos, T> publicVisibleMap;

        private T get(BlockPos pos) {
            return posMap.get(pos);
        }

        private void put(T t, BlockPos pos) {
            posMap.put(pos, t);
            if (hasCallbacks) {
                for (ChangeCallback<T> callback : callbacks) {
                    callback.onChange(t, pos, true);
                }
            }
        }

        private void remove(BlockPos pos) {
            T t = posMap.remove(pos);
            if (hasCallbacks) {
                for (ChangeCallback<T> callback : callbacks) {
                    callback.onChange(t, pos, false);
                }
            }
        }

        private void clear() {
            posMap.clear();
        }

    }

    /**
     * An callback for when the {@link PositionedObjectHolder} has changed
     */
    public interface ChangeCallback<T> {

        /**
         * Gets called when the contents at the specified location have changed
         *
         * @param object The new object in case of an addition, the old object in case of a removal
         * @param pos The position that has changed
         * @param add True if the object was added, false if the object was removed
         */
        public void onChange(T object, BlockPos pos, boolean add);

    }

    public static <T> PositionedObjectHolder<T> immutableCopy(PositionedObjectHolder<T> original) {

        return new PositionedObjectHolder<T>(original.positionedMap) {

            @Override
            public void put(T t, BlockPos pos) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void remove(BlockPos pos) {
                throw new UnsupportedOperationException();
            }

            @Override
            public void clear() {
                throw new UnsupportedOperationException();
            }

        };

    }

}
