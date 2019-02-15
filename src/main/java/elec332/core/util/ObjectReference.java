package elec332.core.util;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 4-1-2019
 */
public class ObjectReference<T> implements Supplier<T>, Consumer<T> {

    public static <T> ObjectReference<T> of(@Nullable T ref) {
        ObjectReference<T> ret = new ObjectReference<>();
        ret.set(ref);
        return ret;
    }

    public ObjectReference() {
        this.ref = null;
    }

    private T ref;

    public void set(T obj) {
        this.ref = obj;
    }

    @Override
    public T get() {
        return this.ref;
    }

    @Override
    public void accept(T t) {
        set(t);
    }

}
