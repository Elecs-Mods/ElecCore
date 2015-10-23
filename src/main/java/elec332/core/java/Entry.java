package elec332.core.java;

import java.util.Map;

/**
 * Created by Elec332 on 23-10-2015.
 */
public class Entry<K, V> implements Map.Entry<K, V> {

    public static <K, V> Map.Entry<K, V> newEntry(K k, V v){
        return new Entry<K, V>(k, v);
    }

    private Entry(K k, V v){
        this.k = k;
        this.v = v;
    }

    private final K k;
    private V v;

    @Override
    public K getKey() {
        return k;
    }

    @Override
    public V getValue() {
        return v;
    }

    @Override
    public V setValue(V value) {
        V old = this.v;
        this.v = value;
        return old;
    }
}
