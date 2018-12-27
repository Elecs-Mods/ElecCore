package elec332.core.api.callback;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 29-9-2016.
 * <p>
 * Used to annotate a class that implements {@link ICallbackProcessor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CallbackProcessor {
}
