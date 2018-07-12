package elec332.core.api.registry;

import com.google.common.collect.*;
import elec332.core.java.JavaHelper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 16-10-2016.
 */
@SuppressWarnings("all")
public class SimpleRegistries {

    public static <T> ISingleObjectRegistry<T> newSingleObjectRegistry(){
        return newSingleObjectRegistry(t -> true);
    }

    public static <T> ISingleObjectRegistry<T> newSingleObjectRegistry(Predicate<T>... predicates){
        return newSingleObjectRegistry(JavaHelper.combine(predicates));
    }

    public static <T> ISingleObjectRegistry<T> newSingleObjectRegistry(Predicate<T> predicate){
        return new DefaultSingleObjectRegistry<T>(predicate);
    }

    public static <T> ISingleObjectRegistry<T> emptySingleObjectRegistry(){
        return NULL_SINGLE_REGISTRY;
    }

    public static <T, V> IDualObjectRegistry<T, V> newDualObjectRegistry(){
        return newDualObjectRegistry(tvPair -> true);
    }

    public static <T, V> IDualObjectRegistry<T, V> newDualObjectRegistry(Predicate<Pair<T, V>>... predicates){
        return newDualObjectRegistry(JavaHelper.combine(predicates));
    }

    public static <T, V> IDualObjectRegistry<T, V> newDualObjectRegistry(Predicate<Pair<T, V>> predicate){
        return new DefaultDualObjectRegistry<T, V>(predicate);
    }

    public static <T, V> IDualObjectRegistry<T, V> emptyDualObjectRegistry(Predicate<Pair<T, V>>... predicates){
        return NULL_DUAL_REGISTRY;
    }

    private static class DefaultSingleObjectRegistry<T> implements ISingleObjectRegistry<T> {

        private DefaultSingleObjectRegistry(Predicate<T> predicate){
            this.type = Sets.newHashSet();
            this.immutableSet = Collections.unmodifiableSet(type);
            this.predicate = predicate;
        }

        private final Set<T> type, immutableSet;
        private final Predicate<T> predicate;

        @Override
        public boolean register(T t){
            return predicate.test(t) && type.add(t);
        }

        @Override
        public Set<T> getAllRegisteredObjects(){
            return immutableSet;
        }

    }

    private static class DefaultDualObjectRegistry<T, V> implements IDualObjectRegistry<T, V> {

        private DefaultDualObjectRegistry(Predicate<Pair<T, V>> predicate) {
            this.map = Maps.newHashMap();
            this.map_ = Collections.unmodifiableMap(map);
            this.predicate = predicate;
        }

        private final Predicate<Pair<T, V>> predicate;
        private final Map<T, V> map, map_;

        @Override
        public boolean register(T t, V v) {
            if (predicate.test(Pair.of(t, v))) {
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

    private static final ISingleObjectRegistry NULL_SINGLE_REGISTRY;
    private static final IDualObjectRegistry NULL_DUAL_REGISTRY;

    static {
        NULL_SINGLE_REGISTRY = new ISingleObjectRegistry() {

            @Override
            public boolean register(Object o) {
                return false;
            }

            @Override
            public Set getAllRegisteredObjects() {
                return ImmutableSet.of();
            }

        };
         NULL_DUAL_REGISTRY= new IDualObjectRegistry() {

            @Override
            public boolean register(Object o, Object o2) {
                return false;
            }

            @Override
            public Map getAllRegisteredObjects() {
                return ImmutableMap.of();
            }

        };
    }

}
