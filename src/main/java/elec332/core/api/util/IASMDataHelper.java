package elec332.core.api.util;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Elec332 on 7-3-2016.
 */
public interface IASMDataHelper {

    public ASMDataTable getASMDataTable();

    public Set<ASMDataTable.ASMData> getAnnotationList(Class<? extends Annotation> annotationClass);

}
