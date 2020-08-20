package elec332.core.api.annotations;

import net.minecraftforge.api.distmarker.Dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 29-10-2016.
 * <p>
 * Loads the annotated class (So the static initializer block will get called) after preInit, but before init
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StaticLoad {

    Dist[] onlyIn() default {};

    int weight() default Byte.MAX_VALUE;

}
