package elec332.core.api.registration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 30-1-2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NamedFieldGetter {

    public Class<?> declaringClass() default Object.class;

    public String field() default "";

    public String name();

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    public @interface Definition {

        public Class<?> declaringClass();

        public String field();

    }

}
