package elec332.core.world.posmap;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 8-11-2017.
 * <p>
 * Default implementation of {@link IMultiWorldPositionedObjectHolder}
 */
public abstract class DefaultMultiWorldPositionedObjectHolder<T, V> implements IMultiWorldPositionedObjectHolder<T, V>, Supplier<IMultiWorldPositionedObjectHolder<T, V>> {

    @SafeVarargs
    public static <T> DefaultMultiWorldPositionedObjectHolder<T, T> create(Consumer<PositionedObjectHolder<T, T>>... callbacks) {
        DefaultMultiWorldPositionedObjectHolder<T, T> ret = new DefaultMultiWorldPositionedObjectHolder<T, T>() {

            @Nonnull
            @Override
            protected PositionedObjectHolder<T, T> createNew() {
                return PositionedObjectHolder.create();
            }

        };
        if (callbacks != null) {
            ret.callbacks.addAll(Arrays.asList(callbacks));
        }
        return ret;
    }

    @SafeVarargs
    public static <T> DefaultMultiWorldPositionedObjectHolder<Set<T>, T> createListed(Consumer<PositionedObjectHolder<Set<T>, T>>... callbacks) {
        DefaultMultiWorldPositionedObjectHolder<Set<T>, T> ret = new DefaultMultiWorldPositionedObjectHolder<Set<T>, T>() {

            @Nonnull
            @Override
            protected PositionedObjectHolder<Set<T>, T> createNew() {
                return new PositionedObjectHolder<>(PositionedObjectHolder.MultiMapPositionChunk::new);
            }

        };
        if (callbacks != null) {
            ret.callbacks.addAll(Arrays.asList(callbacks));
        }
        return ret;
    }

    public DefaultMultiWorldPositionedObjectHolder(Consumer<PositionedObjectHolder<T, V>> callback) {
        this();
        callbacks.add(callback);
    }

    public DefaultMultiWorldPositionedObjectHolder() {
        this.objectsInternal = Maps.newIdentityHashMap();
        this.view = Collections.unmodifiableMap(this.objectsInternal);
        this.callbacks = Lists.newArrayList();
    }

    private final List<Consumer<PositionedObjectHolder<T, V>>> callbacks;
    private final Map<RegistryKey<World>, PositionedObjectHolder<T, V>> objectsInternal;
    private final Map<RegistryKey<World>, PositionedObjectHolder<T, V>> view;

    @Nullable
    @Override
    public PositionedObjectHolder<T, V> get(RegistryKey<World> world) {
        return objectsInternal.get(world);
    }

    @Nonnull
    @Override
    public PositionedObjectHolder<T, V> getOrCreate(RegistryKey<World> world) {
        PositionedObjectHolder<T, V> ret = get(world);
        if (ret == null) {
            ret = createNew();
            for (Consumer<PositionedObjectHolder<T, V>> callback : callbacks) {
                callback.accept(ret);
            }
            objectsInternal.put(world, ret);
        }
        return ret;
    }

    @Nonnull
    @Override
    public Collection<PositionedObjectHolder<T, V>> getValues() {
        return objectsInternal.values();
    }

    @Nonnull
    @Override
    public Map<RegistryKey<World>, PositionedObjectHolder<T, V>> getUnModifiableView() {
        return view;
    }

    @Override
    public void addCreateCallback(Consumer<PositionedObjectHolder<T, V>> callback) {
        callbacks.add(callback);
    }

    @Override
    public void clear() {
        objectsInternal.values().forEach(PositionedObjectHolder::clear);
        objectsInternal.clear();
    }

    @Nonnull
    protected abstract PositionedObjectHolder<T, V> createNew();

    @Override
    public IMultiWorldPositionedObjectHolder<T, V> get() {
        return this;
    }

}
