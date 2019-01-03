package elec332.core.api.mod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class was present in forge versions prior to MC 1.13
 *
 * @author cpw
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface SidedProxy {
    /**
     * The full name of the client side class to load and populate.
     * Defaults to the nested class named "ClientProxy" in the current class.
     */
    String clientSide() default "";

    /**
     * The full name of the server side class to load and populate.
     * Defaults to the nested class named "ServerProxy" in the current class.
     */
    String serverSide() default "";

    /**
     * The name of a mod to load this proxy for. This is required if this annotation is not in the class with @Mod annotation.
     * Or there is no other way to determine the mod this annotation belongs to. When in doubt, add this value.
     */
    String modId() default "";
}
