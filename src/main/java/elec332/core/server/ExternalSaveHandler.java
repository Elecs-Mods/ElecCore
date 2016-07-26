package elec332.core.server;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 5-7-2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ExternalSaveHandler {

    /**
     * The name for this SaveHandler
     *
     * @return The name for this SaveHandler
     */
    public String value() default "";

}
