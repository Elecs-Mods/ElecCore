package elec332.core.api.discovery;

import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import org.objectweb.asm.Type;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Elec332 on 29-10-2016.
 * <p>
 * Works like the legacy ASMData with more features for easier handling
 */
public interface IAnnotationData {

    public ModFile getFile();

    default public String getAnnotationName() {
        return getAnnotationType().toString();
    }

    public Type getAnnotationType();

    public Map<String, Object> getAnnotationInfo();

    default public String getClassName() {
        return getClassType().getClassName();
    }

    @Nullable
    default public Class<?> tryLoadClass() {
        if (hasWrongSideOnlyAnnotation()) {
            return null;
        }
        return loadClass();
    }

    public Class<?> loadClass();

    public Type getClassType();

    public String getMemberName();

    public boolean isField();

    public String getFieldName();

    public Field getField();

    public Class<?> getFieldType();

    public boolean isMethod();

    public String getMethodName();

    public Method getMethod();

    public Type[] getMethodParameterTypes();

    public Class<?>[] getMethodParameters();

    public boolean hasWrongSideOnlyAnnotation();

}
