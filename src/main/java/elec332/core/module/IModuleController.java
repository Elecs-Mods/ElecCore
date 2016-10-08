package elec332.core.module;

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

    default public IModuleInfo getModuleInfo(ASMDataTable.ASMData data) throws Exception {
        Map<String, Object> ann = data.getAnnotationInfo();
        Object o = ann.get("autoDisableIfRequirementsNotMet");
        boolean autoDisableIfRequirementsNotMet = o == null || (boolean) o;
        o = ann.get("alwaysEnabled");
        boolean alwaysOn = o != null && (boolean) o;
        return new DefaultModuleInfo((String) ann.get("owner"), (String) ann.get("name"), (String) ann.get("modDependencies"), (String) ann.get("moduleDependencies"), autoDisableIfRequirementsNotMet, alwaysOn, data.getClassName(), this);
    }

    @Nullable
    default public IModuleContainer wrap(@Nonnull IModuleInfo module) throws Exception {
        return new WrappedModule(Class.forName(module.getModuleClass()).newInstance(), module);
    }

    default public void registerAdditionalModules(Consumer<IModuleInfo> registry){
    }

}
