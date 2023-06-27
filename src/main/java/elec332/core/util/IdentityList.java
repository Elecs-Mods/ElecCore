package elec332.core.util;

import javax.annotation.Nonnull;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Elec332 on 17-2-2019
 *
 * @see sun.awt.util.IdentityArrayList
 */
@SuppressWarnings("unused")
public class IdentityList<E> extends AbstractList<E> {

    public IdentityList(int var1) {
        if (var1 < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + var1);
        } else {
            this.elementData = new Object[var1];
        }
    }

    public IdentityList() {
        this(10);
    }

    public IdentityList(Collection<? extends E> var1) {
        this.elementData = var1.toArray();
        this.size = this.elementData.length;
        if (this.elementData.getClass() != Object[].class) {
            this.elementData = Arrays.copyOf(this.elementData, this.size, Object[].class);
        }
    }

    private transient Object[] elementData;
    private int size;

    public void trimToSize() {
        ++this.modCount;
        int var1 = this.elementData.length;
        if (this.size < var1) {
            this.elementData = Arrays.copyOf(this.elementData, this.size);
        }
    }

    public void ensureCapacity(int var1) {
        ++this.modCount;
        int var2 = this.elementData.length;
        if (var1 > var2) {
            int var4 = var2 * 3 / 2 + 1;
            if (var4 < var1) {
                var4 = var1;
            }
            this.elementData = Arrays.copyOf(this.elementData, var4);
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    @SuppressWarnings("ListIndexOfReplaceableByContains")
    public boolean contains(Object var1) {
        return this.indexOf(var1) >= 0;
    }

    @Override
    public int indexOf(Object var1) {
        for (int var2 = 0; var2 < this.size; ++var2) {
            if (var1 == this.elementData[var2]) {
                return var2;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object var1) {
        for (int var2 = this.size - 1; var2 >= 0; --var2) {
            if (var1 == this.elementData[var2]) {
                return var2;
            }
        }
        return -1;
    }

    @Nonnull
    @Override
    public Object[] toArray() {
        return Arrays.copyOf(this.elementData, this.size);
    }

    @Nonnull
    @Override
    @SuppressWarnings("all")
    public <T> T[] toArray(@Nonnull T[] var1) {
        if (var1.length < this.size) {
            return (T[]) Arrays.copyOf(this.elementData, this.size, var1.getClass());
        } else {
            System.arraycopy(this.elementData, 0, var1, 0, this.size);
            if (var1.length > this.size) {
                var1[this.size] = null;
            }
            return var1;
        }
    }

    @Override
    @SuppressWarnings("all")
    public E get(int var1) {
        this.rangeCheck(var1);
        return (E) this.elementData[var1];
    }

    @Override
    @SuppressWarnings("all")
    public E set(int var1, E var2) {
        this.rangeCheck(var1);
        Object var3 = this.elementData[var1];
        this.elementData[var1] = var2;
        return (E) var3;
    }

    @Override
    public boolean add(E var1) {
        this.ensureCapacity(this.size + 1);
        this.elementData[this.size++] = var1;
        return true;
    }

    @Override
    public void add(int var1, E var2) {
        this.rangeCheckForAdd(var1);
        this.ensureCapacity(this.size + 1);
        System.arraycopy(this.elementData, var1, this.elementData, var1 + 1, this.size - var1);
        this.elementData[var1] = var2;
        ++this.size;
    }

    @Override
    @SuppressWarnings("all")
    public E remove(int var1) {
        this.rangeCheck(var1);
        ++this.modCount;
        Object var2 = this.elementData[var1];
        int var3 = this.size - var1 - 1;
        if (var3 > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var3);
        }
        this.elementData[--this.size] = null;
        return (E) var2;
    }

    @Override
    public boolean remove(Object var1) {
        for (int var2 = 0; var2 < this.size; ++var2) {
            if (var1 == this.elementData[var2]) {
                this.fastRemove(var2);
                return true;
            }
        }
        return false;
    }

    private void fastRemove(int var1) {
        ++this.modCount;
        int var2 = this.size - var1 - 1;
        if (var2 > 0) {
            System.arraycopy(this.elementData, var1 + 1, this.elementData, var1, var2);
        }
        this.elementData[--this.size] = null;
    }

    @Override
    public void clear() {
        ++this.modCount;
        for (int var1 = 0; var1 < this.size; ++var1) {
            this.elementData[var1] = null;
        }
        this.size = 0;
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends E> var1) {
        Object[] var2 = var1.toArray();
        int var3 = var2.length;
        this.ensureCapacity(this.size + var3);
        System.arraycopy(var2, 0, this.elementData, this.size, var3);
        this.size += var3;
        return var3 != 0;
    }

    @Override
    public boolean addAll(int var1, Collection<? extends E> var2) {
        this.rangeCheckForAdd(var1);
        Object[] var3 = var2.toArray();
        int var4 = var3.length;
        this.ensureCapacity(this.size + var4);
        int var5 = this.size - var1;
        if (var5 > 0) {
            System.arraycopy(this.elementData, var1, this.elementData, var1 + var4, var5);
        }
        System.arraycopy(var3, 0, this.elementData, var1, var4);
        this.size += var4;
        return var4 != 0;
    }

    @Override
    protected void removeRange(int var1, int var2) {
        ++this.modCount;
        int var3 = this.size - var2;
        System.arraycopy(this.elementData, var2, this.elementData, var1, var3);
        int var4 = this.size - (var2 - var1);
        while (this.size != var4) {
            this.elementData[--this.size] = null;
        }

    }

    private void rangeCheck(int var1) {
        if (var1 >= this.size) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
        }
    }

    private void rangeCheckForAdd(int var1) {
        if (var1 > this.size || var1 < 0) {
            throw new IndexOutOfBoundsException(this.outOfBoundsMsg(var1));
        }
    }

    private String outOfBoundsMsg(int var1) {
        return "Index: " + var1 + ", Size: " + this.size;
    }

}
