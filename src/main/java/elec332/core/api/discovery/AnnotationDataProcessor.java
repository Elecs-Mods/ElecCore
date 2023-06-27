package elec332.core.api.discovery;

import net.minecraftforge.fml.ModLoadingStage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 7-3-2016.
 * <p>
 * Used to annotate a class that implements {@link IAnnotationDataProcessor}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AnnotationDataProcessor {

    /**
     * Valid ModLoadingStages:
     * ModLoadingStage.CONSTRUCTING
     * ModLoadingStage.PREINITIALIZATION
     * ModLoadingStage.INITIALIZATION
     * ModLoadingStage.POSTINITIALIZATION
     * ModLoadingStage.AVAILABLE
     *
     * @return The (array of) ModLoadingStage(s) in which to load this.
     */
    ModLoadingStage[] value();

    /**
     * The importance of this {@link IAnnotationDataProcessor}, higher = earlier processing
     *
     * @return Importance of this {@link IAnnotationDataProcessor}
     */
    int importance() default -1;

}
