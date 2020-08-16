package elec332.core.api.discovery;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModFileInfo;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 7-3-2016.
 * <p>
 * Handles annotation data
 */
public interface IAnnotationDataHandler {

    default Set<IAnnotationData> getAnnotationList(Class<? extends Annotation> annotationClass) {
        return getAnnotationList(Type.getType(annotationClass));
    }

    Set<IAnnotationData> getAnnotationList(Type annotationType);

    boolean hasWrongSideOnlyAnnotation(String clazz);

    Function<Type, Set<IAnnotationData>> getAnnotationsFor(IModFileInfo file);

    Function<Type, Set<IAnnotationData>> getAnnotationsFor(ModContainer mc);

    ModContainer deepSearchOwner(IAnnotationData annotationData);

    String deepSearchOwnerName(IAnnotationData annotationData);

}
