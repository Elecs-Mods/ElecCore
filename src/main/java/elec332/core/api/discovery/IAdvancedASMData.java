package elec332.core.api.discovery;

import net.minecraftforge.fml.common.discovery.ModCandidate;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by Elec332 on 29-10-2016.
 *
 * Wrapper for {@link net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData}
 * with more features for easier handling
 */
public interface IAdvancedASMData {

    public ModCandidate getContainer();

    public String getAnnotationName();

    public Map<String, Object> getAnnotationInfo();

    public String getClassName();

    public Class<?> loadClass();

    public boolean isField();

    public String getFieldName();

    public Field getField();

    public Class<?> getFieldType();

    public boolean isMethod();

    public String getMethodName();

    public Method getMethod();

    public Type[] getMethodParameterTypes();

    public Class<?>[] getMethodParameters();

}
