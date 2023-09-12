package elec332.core.util.function;

/**
 * Created by Elec332 on 10-01-2023
 */
public interface UnsafeConsumer<T, E extends Throwable> {

    void accept(T t) throws E;

}
