package elec332.core.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 4-1-2019
 */
public class ObjectReference<T> implements Supplier<T>, Consumer<T> {

    private T ref;

    public void set(T obj) {
        this.ref = obj;
    }

    @Override
    public T get() {
        return ref;
    }

    @Override
    public void accept(T t) {
        set(t);
    }

}
