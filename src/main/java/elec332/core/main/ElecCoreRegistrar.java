package elec332.core.main;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.util.Pair;
import elec332.core.compat.waila.IWailaCapabilityDataProvider;
import elec332.core.grid.v2.AbstractGridHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 1-8-2016.
 */
@SuppressWarnings("all")
public final class ElecCoreRegistrar {

    private ElecCoreRegistrar(){
        throw new IllegalAccessError();
    }

    public static final ISingleObjectRegistry<AbstractGridHandler> GRIDS_V2;
    public static final IDualObjectRegistry<Capability, IWailaCapabilityDataProvider> WAILA_CAPABILITY_PROVIDER;

    static {
        IN_MOD_LOADING = input -> !Loader.instance().hasReachedState(LoaderState.AVAILABLE);
        WAILA_CAPABILITY_PROVIDER = new DefaultDualObjectRegistry<Capability, IWailaCapabilityDataProvider>(mergePredicates(new Predicate<Pair<Capability, IWailaCapabilityDataProvider>>() {

            @Override
            public boolean apply(@Nullable Pair<Capability, IWailaCapabilityDataProvider> input) {
                return input.second().isCompatibleCapability(input.first());
            }

        }, ElecCoreRegistrar.IN_MOD_LOADING));
        GRIDS_V2 = new DefaultSingleObjectRegistry<AbstractGridHandler>(ElecCoreRegistrar.IN_MOD_LOADING);
    }

    private static final Predicate IN_MOD_LOADING;

    private static final <O> Predicate<O> mergePredicates(Predicate<O> p1, Predicate<O> p2){
        return new Predicate<O>() {

            @Override
            public boolean apply(@Nullable O input) {
                return p1.apply(input) && p2.apply(input);
            }

        };
    }

    public static interface ISingleObjectRegistry<T> {

        public boolean register(T t);

        public Set<T> getAllRegisteredObjects();

    }

    public static interface IDualObjectRegistry<T, V> {

        public boolean register(T t, V v);

        public Map<T, V> getAllRegisteredObjects();

    }

    private static class DefaultSingleObjectRegistry<T> implements ISingleObjectRegistry<T> {

        private DefaultSingleObjectRegistry(){
            this(Predicates.alwaysTrue());
        }

        private DefaultSingleObjectRegistry(Predicate<T> predicate){
            this.type = Sets.newHashSet();
            this.immutableSet = Collections.unmodifiableSet(type);
            this.predicate = predicate;
        }

        private final Set<T> type, immutableSet;
        private final Predicate<T> predicate;

        @Override
        public boolean register(T t){
            return predicate.apply(t) && type.add(t);
        }

        @Override
        public Set<T> getAllRegisteredObjects(){
            return immutableSet;
        }

    }

    private static class DefaultDualObjectRegistry<T, V> implements IDualObjectRegistry<T, V> {

        private DefaultDualObjectRegistry(){
            this(Predicates.alwaysTrue());
        }

        private DefaultDualObjectRegistry(Predicate<Pair<T, V>> predicate){
            this.map = Maps.newHashMap();
            this.map_ = Collections.unmodifiableMap(map);
            this.predicate = predicate;
        }

        private final Predicate<Pair<T, V>> predicate;
        private final Map<T, V> map, map_;

        @Override
        public boolean register(T t, V v) {
            if (predicate.apply(Pair.of(t, v))){
                map.put(t, v);
                return true;
            }
            return false;
        }

        @Override
        public Map<T, V> getAllRegisteredObjects() {
            return map_;
        }
    }

    static void dummyLoad(){
    }

}
