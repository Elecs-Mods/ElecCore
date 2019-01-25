package elec332.core.util.function;

/**
 * Created by Elec332 on 20-1-2019
 */
public class FuncHelper {

    public static Runnable safeRunnable(UnsafeRunnable runnable){
        return () -> {
            try {
                runnable.run();
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        };
    }

}
