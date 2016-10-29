package elec332.core.api.module;

import net.minecraftforge.fml.common.discovery.ASMDataTable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleController {

    public boolean isModuleEnabled(String moduleName);

    default public IModuleInfo getModuleInfo(ASMDataTable.ASMData data, IDefaultModuleImplementations defaultModuleImplementations) throws Exception {
        Map<String, Object> ann = data.getAnnotationInfo();
        Object o = ann.get("autoDisableIfRequirementsNotMet");
        boolean autoDisableIfRequirementsNotMet = o == null || (boolean) o;
        o = ann.get("alwaysEnabled");
        boolean alwaysOn = o != null && (boolean) o;
        return defaultModuleImplementations.newDefaultModuleInfo((String) ann.get("owner"), (String) ann.get("name"), (String) ann.get("modDependencies"), (String) ann.get("moduleDependencies"), autoDisableIfRequirementsNotMet, alwaysOn, data.getClassName(), this);
    }

    @Nullable
    default public IModuleContainer wrap(@Nonnull IModuleInfo module, IDefaultModuleImplementations defaultModuleImplementations) throws Exception {
        return defaultModuleImplementations.newDefaultModuleContainer(Class.forName(module.getModuleClass()).newInstance(), module);
    }

    default public void registerAdditionalModules(Consumer<IModuleInfo> registry){
    }



}
