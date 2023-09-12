package elec332.core.util.function;

/**
 * Created by Elec332 on 20-1-2019
 */
public interface UnsafeRunnable<E extends Throwable> {

    void run() throws E;

}
