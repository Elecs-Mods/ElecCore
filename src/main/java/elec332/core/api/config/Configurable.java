package elec332.core.api.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 9-4-2015.
 * <p>
 * Used to annotate configurable fields (or methods) in your configuration class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Configurable {

    /**
     * @return The config category of this field
     */
    String category() default "";

    /**
     * @return The comment on this configurable value
     */
    String comment() default "";

    /**
     * @return The min value of you field (If your field is a number)
     */
    float minValue() default 0;

    /**
     * @return The max value of you field (If your field is a number)
     */
    float maxValue() default 0;

    /**
     * @return An array of valid {@link String}'s of you field (If your field is a String/Enum)
     */
    String[] validStrings() default {};

    /**
     * @return If this method is enabled by default (only used on methods)
     */
    boolean enabledByDefault() default true;

    /**
     * Used to annotate config classes, can be used to set a category for all fields in the annotated class,
     * and can also be used to create comments for this category.
     * <p>
     * If {@param inherit} is true, this category will be a sub-category of the superclass (if it is annotated with {@link Configurable.Class})
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Class {

        String category() default "";

        String comment() default "";

        boolean inherit() default true;

    }

}
