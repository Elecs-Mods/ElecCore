package elec332.core.util.function;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 20-1-2019
 */
public class FuncHelper {

    public static Runnable safeRunnable(UnsafeRunnable<?> runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Supplier<T> safeSupplier(UnsafeSupplier<T, ?> supplier) {
        return () -> {
            try {
                return supplier.get();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T> Consumer<T> safeConsumer(UnsafeConsumer<T, ?> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, U> BiConsumer<T, U> safeBiConsumer(UnsafeBiConsumer<T, U, ?> biConsumer) {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    public static <T, R> Function<T, R> safeFunction(UnsafeFunction<T, R, ?> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

}
