package elec332.core.util.function;

/**
 * Created by Elec332 on 10-01-2023
 */
public interface UnsafeFunction<T, R, E extends Throwable> {

    R apply(T t) throws E;

}
