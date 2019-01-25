package elec332.core.api.discovery;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModFileInfo;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 7-3-2016.
 * <p>
 * Handles annotation data
 */
public interface IAnnotationDataHandler {

    default public Set<IAnnotationData> getAnnotationList(Class<? extends Annotation> annotationClass) {
        return getAnnotationList(Type.getType(annotationClass));
    }

    public Set<IAnnotationData> getAnnotationList(Type annotationType);

    public Function<Type, Set<IAnnotationData>> getAnnotationsFor(IModFileInfo file);

    public Function<Type, Set<IAnnotationData>> getAnnotationsFor(ModContainer mc);

    public ModContainer deepSearchOwner(IAnnotationData annotationData);

    public String deepSearchOwnerName(IAnnotationData annotationData);

}
