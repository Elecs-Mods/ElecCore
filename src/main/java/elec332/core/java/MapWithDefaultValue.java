package elec332.core.java;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class MapWithDefaultValue<K, V> implements Map<K, V> {

    public static <K, V> MapWithDefaultValue<K, V> newMap(Map<K, V> map, Callable<V> newDefaultValueCreator){
        return new MapWithDefaultValue<K, V>(map, newDefaultValueCreator);
    }

    private MapWithDefaultValue(Map<K, V> map, Callable<V> newDefaultValueCreator){
        this.map = Preconditions.checkNotNull(map);
        this.c = Preconditions.checkNotNull(newDefaultValueCreator);
    }

    private final Map<K, V> map;
    private final Callable<V> c;

    @Override
    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(Object key) {
        V ret = map.get(key);
        if (ret == null){
            try {
                ret = c.call();
                map.put((K) key, ret);
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return ret;
    }

    @Override
    public V put(K key, V value) {
        return map.put(key, value);
    }

    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends K, ? extends V> m) {
        map.putAll(m);
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    @Nonnull
    public Set<K> keySet() {
        return map.keySet();
    }

    @Override
    @Nonnull
    public Collection<V> values() {
        return map.values();
    }

    @Override
    @Nonnull
    public Set<Entry<K, V>> entrySet() {
        return map.entrySet();
    }

}
