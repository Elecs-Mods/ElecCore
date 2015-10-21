package elec332.core.config;

import net.minecraftforge.common.config.Configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 9-4-2015.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Configurable {

    String category() default Configuration.CATEGORY_GENERAL;

    String comment() default "";

    int minValue() default 0;

    int maxValue() default 0;

    String[] validStrings() default {};

    boolean enabledByDefault() default true;


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Class{

        String category() default Configuration.CATEGORY_GENERAL;

        String comment() default "";

        /* No Booleans allowed :( */
        Inherit inherit() default Inherit.TRUE;

    }

    public enum Inherit{
        TRUE, FALSE
    }

}
