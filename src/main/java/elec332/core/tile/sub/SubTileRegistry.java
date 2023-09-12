package elec332.core.tile.sub;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 20-2-2018
 */
public enum SubTileRegistry {

    INSTANCE;

    private final Map<ResourceLocation, Class<? extends SubTileLogicBase>> registry = Maps.newHashMap();
    private final Map<Class<? extends SubTileLogicBase>, ResourceLocation> registryInverse = Maps.newHashMap();
    private final Map<ResourceLocation, Function<SubTileLogicBase.Data, SubTileLogicBase>> constructors = Maps.newHashMap();
    private final Map<Capability, Function<List<?>, ?>> capCombiners = Maps.newHashMap();
    private final Set<Capability> cacheables = Sets.newHashSet();

    @SuppressWarnings("all")
    public void registerSubTile(@Nonnull Class<? extends ISubTileLogic> clazz, @Nonnull ResourceLocation name) {
        if (!SubTileLogicBase.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException();
        }
        Preconditions.checkNotNull(name);
        if (registry.get(name) != null) {
            throw new UnsupportedOperationException();
        }
        try {
            MethodHandle handle = MethodHandles.lookup().unreflectConstructor(clazz.getConstructor(SubTileLogicBase.Data.class));
            constructors.put(name, data -> {
                try {
                    return (SubTileLogicBase) handle.invoke(data);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            });
            registry.put(name, (Class<? extends SubTileLogicBase>) clazz);
            registryInverse.put((Class<? extends SubTileLogicBase>) clazz, name);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public SubTileLogicBase invoke(@Nonnull Class<? extends ISubTileLogic> clazz, @Nonnull SubTileLogicBase.Data data) {
        if (!registryInverse.containsKey(clazz)) {
            throw new IllegalArgumentException();
        }
        return constructors.get(registryInverse.get(clazz)).apply(data);
    }

    @Nonnull
    public SubTileLogicBase invoke(@Nonnull ResourceLocation name, @Nonnull SubTileLogicBase.Data data) {
        if (!constructors.containsKey(name)) {
            throw new IllegalArgumentException();
        }
        return constructors.get(name).apply(data);
    }

    @Nonnull
    public ResourceLocation getRegistryName(@Nonnull Class<? extends ISubTileLogic> clazz) {
        if (!registryInverse.containsKey(clazz)) {
            throw new IllegalArgumentException();
        }
        return Preconditions.checkNotNull(registryInverse.get(clazz));
    }

    public void setCapabilityCacheable(Capability<?> cap) {
        cacheables.add(cap);
    }

    public boolean isCacheable(Capability<?> cap) {
        return cacheables.contains(cap);
    }

    public <T> void registerCapabilityInstanceCombiner(@Nonnull Capability<T> capability, Function<List<T>, T> combiner) {
        SubTileRegistry.INSTANCE.registerCapabilityCombiner(capability, lazyOptionals -> {
            List<T> objects = Lists.newArrayList();
            lazyOptionals.forEach(c -> {
                if (!c.isPresent()) {
                    throw new RuntimeException();
                }
                objects.add(c.orElseThrow(NullPointerException::new));
            });
            final T r = combiner.apply(objects);
            LazyOptional<T> ret = LazyOptional.of(() -> r);
            lazyOptionals.forEach(lo -> lo.addListener(lo2 -> ret.invalidate()));
            return ret;
        });
    }

    @SuppressWarnings("unchecked")
    public <T> void registerCapabilityCombiner(@Nonnull Capability<T> capability, @Nonnull Function<List<LazyOptional<T>>, LazyOptional<T>> combiner) {
        Preconditions.checkNotNull(capability, "Capability cannot be null!");
        if (capCombiners.containsKey(capability)) {
            throw new IllegalArgumentException();
        }
        capCombiners.put(capability, (Function) combiner);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T> LazyOptional<T> getCombined(@Nonnull Capability<T> capability, @Nonnull List<LazyOptional<T>> list) {
        list = list.stream()
                .filter(Objects::nonNull)
                .filter(LazyOptional::isPresent)
                .collect(Collectors.toList());
        if (list.isEmpty()) {
            return LazyOptional.empty();
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        Function<List<?>, ?> func = capCombiners.get(capability);
        if (func == null) {
            throw new IllegalArgumentException("No combiner registered for capability: " + capability.getName());
        }
        final LazyOptional<T> ret = (LazyOptional<T>) func.apply(list);
        list.forEach(o -> o.addListener(v -> ret.invalidate()));
        return ret;
    }

}
