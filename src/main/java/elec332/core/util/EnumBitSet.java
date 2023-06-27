package elec332.core.util;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Created by Elec332 on 22-1-2018.
 */
public class EnumBitSet<E extends Enum<E>> extends AbstractSet<E> implements Cloneable {

    @SuppressWarnings("unchecked")
    public static <E extends Enum<E>> EnumBitSet<E> of(E one, E... values) {
        EnumBitSet<E> ret = noneOf(one.getDeclaringClass());
        ret.add(one);
        ret.addAll(Arrays.asList(values));
        return ret;
    }

    public static <E extends Enum<E>> EnumBitSet<E> noneOf(Class<E> clazz) {
        return new EnumBitSet<>(clazz);
    }

    private final E[] values;
    private final Class<E> type;
    private long elements = 0L;

    private EnumBitSet(Class<E> type) {
        super();
        this.values = type.getEnumConstants();
        this.type = type;
    }

    public long getSerialized() {
        return elements;
    }

    public void deserialize(long serialized) {
        this.elements = serialized;
    }

    @Nonnull
    public Iterator<E> iterator() {
        return new EnumBitSetIterator();
    }

    @Override
    public int size() {
        return Long.bitCount(elements);
    }

    @Override
    public boolean isEmpty() {
        return elements == 0;
    }

    @Override
    public boolean contains(Object e) {
        if (e == null) {
            return false;
        }
        Class<?> eClass = e.getClass();
        return (eClass == type || eClass.getSuperclass() == type) && (elements & (1L << ((Enum<?>) e).ordinal())) != 0;
    }

    @Override
    public boolean add(E e) {
        typeCheck(e);
        long oldElements = elements;
        elements |= (1L << e.ordinal());
        return elements != oldElements;
    }

    @Override
    public boolean remove(Object e) {
        if (e == null) {
            return false;
        }
        Class<?> eClass = e.getClass();
        if (eClass != type && eClass.getSuperclass() != type) {
            return false;
        }
        long oldElements = elements;
        elements &= ~(1L << ((Enum<?>) e).ordinal());
        return elements != oldElements;
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        if (!(c instanceof EnumBitSet)) {
            return super.containsAll(c);
        }
        EnumBitSet<?> es = (EnumBitSet<?>) c;
        if (es.type != type) {
            return es.isEmpty();
        }
        return (es.elements & ~elements) == 0;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> c) {
        if (!(c instanceof EnumBitSet)) {
            return super.addAll(c);
        }
        EnumBitSet<?> es = (EnumBitSet<?>) c;
        if (es.type != type) {
            if (es.isEmpty()) {
                return false;
            } else {
                throw new ClassCastException(es.type + " != " + type);
            }
        }
        long oldElements = elements;
        elements |= es.elements;
        return elements != oldElements;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (!(c instanceof EnumBitSet)) {
            return super.removeAll(c);
        }
        EnumBitSet<?> es = (EnumBitSet<?>) c;
        if (es.type != type) {
            return false;
        }
        long oldElements = elements;
        elements &= ~es.elements;
        return elements != oldElements;
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        if (!(c instanceof EnumBitSet)) {
            return super.retainAll(c);
        }
        EnumBitSet<?> es = (EnumBitSet<?>) c;
        if (es.type != type) {
            boolean changed = (elements != 0);
            elements = 0;
            return changed;
        }
        long oldElements = elements;
        elements &= es.elements;
        return elements != oldElements;
    }

    @Override
    public void clear() {
        elements = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EnumBitSet)) {
            return super.equals(o);
        }
        EnumBitSet<?> es = (EnumBitSet<?>) o;
        if (es.type != type) {
            return elements == 0 && es.elements == 0;
        }
        return es.elements == elements;
    }

    @Override
    @SuppressWarnings("all")
    public EnumBitSet<E> clone() {
        try {
            EnumBitSet<E> ret = new EnumBitSet<E>(type);
            ret.deserialize(elements);
            return ret;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addRange(E from, E to) {
        elements = (-1L >>> (from.ordinal() - to.ordinal() - 1)) << from.ordinal();
    }

    public void addAll() {
        if (values.length != 0) {
            elements = -1L >>> -values.length;
        }
    }

    private void typeCheck(E e) {
        Class<?> eClass = e.getClass();
        if (eClass != type && eClass.getSuperclass() != type) {
            throw new ClassCastException(eClass + " != " + type);
        }
    }

    private class EnumBitSetIterator implements Iterator<E> {

        private EnumBitSetIterator() {
            unseen = elements;
        }

        private long lastReturned = 0, unseen;

        @Override
        public boolean hasNext() {
            return unseen != 0;
        }

        @Override
        public E next() {
            if (unseen == 0) {
                throw new NoSuchElementException();
            }
            lastReturned = unseen & -unseen;
            unseen -= lastReturned;
            return values[Long.numberOfTrailingZeros(lastReturned)];
        }

        @Override
        public void remove() {
            if (lastReturned == 0) {
                throw new IllegalStateException();
            }
            elements &= ~lastReturned;
            lastReturned = 0;
        }

    }

}
