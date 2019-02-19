package elec332.core.world.posmap;

import com.google.common.collect.*;
import elec332.core.api.util.IClearable;
import elec332.core.world.WorldHelper;
import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Created by Elec332 on 4-2-2016.
 * <p>
 * A fast way to store objects in a 3D coordinate based map
 */
public class PositionedObjectHolder<T, V> implements IClearable {

    public static <T> PositionedObjectHolder<T, T> create() {
        return new PositionedObjectHolder<>(PositionChunk::new);
    }

    public PositionedObjectHolder(Function<ChunkPos, AbstractPositionChunk<T, V>> supplier) {
        this(new Long2ObjectLinkedOpenHashMap<>(), supplier);
    }

    private PositionedObjectHolder(Map<Long, AbstractPositionChunk<T, V>> positionedMap, Function<ChunkPos, AbstractPositionChunk<T, V>> supplier) {
        this.positionedMap = positionedMap;
        this.callbacks = Sets.newHashSet();
        this.hasCallbacks = false;
        this.supplier = supplier;
    }

    private final Function<ChunkPos, AbstractPositionChunk<T, V>> supplier;

    private final Map<Long, AbstractPositionChunk<T, V>> positionedMap;
    private final Set<ChangeCallback<V>> callbacks;
    private boolean hasCallbacks;

    /**
     * Clears the entire map, including callbacks!
     */
    @Override
    public void clear() {
        positionedMap.values().forEach(AbstractPositionChunk::clear);
        positionedMap.clear();
        callbacks.clear();
    }

    /**
     * Registers a callback to this map
     *
     * @param callback The callback to be registered
     */
    public void registerCallback(ChangeCallback<V> callback) {
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
    public T get(BlockPos pos) {
        return getChunkForPos(pos).get(pos);
    }

    @Nonnull
    private AbstractPositionChunk<T, V> getChunkForPos(BlockPos pos) {
        return getChunkForPos(new ChunkPos(pos));
    }

    @Nonnull
    private AbstractPositionChunk<T, V> getChunkForPos(ChunkPos chunkPos) {
        long l = WorldHelper.longFromChunkPos(chunkPos);
        AbstractPositionChunk<T, V> positionChunk = positionedMap.get(l);
        if (positionChunk == null) {
            positionChunk = supplier.apply(chunkPos);
            positionedMap.put(l, positionChunk);
        }
        return positionChunk;
    }

    /**
     * Puts an object at the specified position
     *
     * @param t   The object to be stored
     * @param pos The position
     */
    public void put(V t, BlockPos pos) {
        getChunkForPos(pos).put(t, pos);
        if (hasCallbacks) {
            for (ChangeCallback<V> callback : callbacks) {
                callback.onChange(t, pos, true);
            }
        }
    }

    /**
     * Removes the object at the specified coordinated
     *
     * @param pos The position to be cleared
     */
    public void remove(BlockPos pos) {
        AbstractPositionChunk<T, V> chunk = getChunkForPos(pos);
        Iterable<V> t = chunk.remove(pos);
        if (hasCallbacks) {
            for (ChangeCallback<V> callback : callbacks) {
                for (V v : t) {
                    callback.onChange(v, pos, false);
                }
            }
        }
        if (chunk.isEmpty()) {
            positionedMap.remove(WorldHelper.longFromChunkPos(chunk.pos));
        }
    }

    /**
     * Removes the object at the specified coordinated
     *
     * @param pos The position to be cleared
     */
    public void remove(BlockPos pos, V obj) {
        AbstractPositionChunk<T, V> chunk = getChunkForPos(pos);
        boolean b = chunk.remove(pos, obj);
        if (b && hasCallbacks) {
            for (ChangeCallback<V> callback : callbacks) {
                callback.onChange(obj, pos, false);
            }
        }
        if (chunk.isEmpty()) {
            positionedMap.remove(WorldHelper.longFromChunkPos(chunk.pos));
        }
    }

    /**
     * @return Gets all chunk positions that are in this map
     */
    public Set<ChunkPos> getChunks() {
        Set<ChunkPos> ret = Sets.newHashSet();
        for (AbstractPositionChunk chunk : positionedMap.values()) {
            ret.add(chunk.pos);
        }
        return ret;
    }

    @Nonnull
    public Map<BlockPos, T> getObjectsInChunk(ChunkPos chunk) {
        return getChunkForPos(chunk).getView();
    }

    public Stream<V> streamValues() {
        return positionedMap.values().stream().flatMap(AbstractPositionChunk::streamValues);
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
    public static abstract class AbstractPositionChunk<T, V> {

        protected AbstractPositionChunk(ChunkPos pos) {
            this.pos = pos;
        }

        private final ChunkPos pos;

        protected abstract T get(BlockPos pos);

        protected abstract void put(V t, BlockPos pos);

        protected abstract Iterable<V> remove(BlockPos pos);

        protected abstract boolean remove(BlockPos pos, V obj);

        protected abstract boolean isEmpty();

        protected abstract Map<BlockPos, T> getView();

        protected abstract Stream<V> streamValues();

        protected abstract void clear();

    }

    private static class PositionChunk<T> extends AbstractPositionChunk<T, T> {

        private PositionChunk(ChunkPos pos) {
            super(pos);
            this.posMap = Maps.newHashMap();
            this.publicVisibleMap = Collections.unmodifiableMap(this.posMap);
        }

        private final Map<BlockPos, T> posMap;
        private final Map<BlockPos, T> publicVisibleMap;

        @Override
        protected T get(BlockPos pos) {
            return posMap.get(pos);
        }

        @Override
        protected void put(T t, BlockPos pos) {
            posMap.put(pos, t);
        }

        @Override
        protected Iterable<T> remove(BlockPos pos) {
            T ret = posMap.remove(pos);
            if (ret == null) {
                return ImmutableList.of();
            }
            return ImmutableList.of(ret);
        }

        @Override
        protected boolean remove(BlockPos pos, T obj) {
            return posMap.remove(pos, obj);
        }

        @Override
        protected Map<BlockPos, T> getView() {
            return publicVisibleMap;
        }

        @Override
        protected Stream<T> streamValues() {
            return publicVisibleMap.values().stream();
        }

        @Override
        protected boolean isEmpty() {
            return this.posMap.isEmpty();
        }

        @Override
        protected void clear() {
            posMap.clear();
        }

    }

    public static class MultiMapPositionChunk<T> extends AbstractPositionChunk<Set<T>, T> {

        public MultiMapPositionChunk(ChunkPos pos) {
            super(pos);
            this.posMap = HashMultimap.create();
            this.publicVisibleMap = Collections.unmodifiableMap(Multimaps.asMap(this.posMap));
        }

        private final SetMultimap<BlockPos, T> posMap;
        private final Map<BlockPos, Set<T>> publicVisibleMap;

        @Override
        protected Set<T> get(BlockPos pos) {
            return posMap.get(pos);
        }

        @Override
        protected void put(T t, BlockPos pos) {
            posMap.put(pos, t);
        }

        @Override
        protected Collection<T> remove(BlockPos pos) {
            return posMap.removeAll(pos);
        }

        @Override
        protected boolean remove(BlockPos pos, T obj) {
            return posMap.remove(pos, obj);
        }

        @Override
        protected Map<BlockPos, Set<T>> getView() {
            return publicVisibleMap;
        }

        @Override
        protected Stream<T> streamValues() {
            return posMap.values().stream();
        }

        @Override
        protected boolean isEmpty() {
            return this.posMap.isEmpty();
        }

        @Override
        protected void clear() {
            posMap.clear();
        }

    }

    /**
     * An callback for when the {@link PositionedObjectHolder} has changed
     */
    public interface ChangeCallback<V> {

        /**
         * Gets called when the contents at the specified location have changed
         *
         * @param object The new object in case of an addition, the old object in case of a removal
         * @param pos    The position that has changed
         * @param add    True if the object was added, false if the object was removed
         */
        public void onChange(V object, BlockPos pos, boolean add);

    }

    public static <T, V> PositionedObjectHolder<T, V> immutableCopy(PositionedObjectHolder<T, V> original) {

        return new PositionedObjectHolder<T, V>(original.positionedMap, p -> {
            throw new UnsupportedOperationException();
        }) {

            @Override
            public void put(V t, BlockPos pos) {
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

            @Override
            public void remove(BlockPos pos, V obj) {
                throw new UnsupportedOperationException();
            }

        };

    }

}
