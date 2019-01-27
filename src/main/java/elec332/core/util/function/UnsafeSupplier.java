package elec332.core.util.function;

/**
 * Created by Elec332 on 26-1-2019
 */
public interface UnsafeSupplier<T> {

    public T get() throws Exception;

}
