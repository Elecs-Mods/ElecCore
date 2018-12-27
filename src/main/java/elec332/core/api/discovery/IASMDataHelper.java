package elec332.core.api.discovery;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Elec332 on 7-3-2016.
 * <p>
 * Wrapper for {@link ASMDataTable} with a few extra features
 */
public interface IASMDataHelper {

    public ASMDataTable getASMDataTable();

    public Set<ASMDataTable.ASMData> getAnnotationList(Class<? extends Annotation> annotationClass);

    public Set<IAdvancedASMData> getAdvancedAnnotationList(Class<? extends Annotation> annotationClass);

}
