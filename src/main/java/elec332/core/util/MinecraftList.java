package elec332.core.util;

import elec332.core.api.annotations.AbstractionMarker;

import java.util.AbstractList;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
public final class MinecraftList<E> extends AbstractList<E> {

    public static <E> MinecraftList<E> create(){
        return new MinecraftList<E>(createList());
    }

    @SuppressWarnings("unchecked")
    public static <E> MinecraftList<E> create(int size, E defaultObj){
        return new MinecraftList<E>(createList(size, defaultObj));
    }

    @AbstractionMarker("getInventoryAbstraction")
    private static <E> List<E> createList(){
        throw new UnsupportedOperationException();
    }

    @AbstractionMarker("getInventoryAbstraction")
    private static <E> List<E> createList(int size, E defaultObj){
        throw new UnsupportedOperationException();
    }

    private MinecraftList(List<E> underlyingList){
        this.underlyingList = underlyingList;
    }

    private final List<E> underlyingList;

    public List<E> getUnderlyingList(){
        return underlyingList;
    }

    @Override
    public E get(int index) {
        return underlyingList.get(index);
    }

    @Override
    public E set(int index, E element) {
        return underlyingList.set(index, element);
    }

    @Override
    public boolean add(E e) {
        return underlyingList.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return underlyingList.remove(o);
    }

    @Override
    public int size() {
        return underlyingList.size();
    }

    @Override
    public void clear() {
        underlyingList.clear();
    }

}
