package elec332.core.api.module;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleController {

    default public boolean shouldModuleConstruct(IModuleInfo moduleInfo, boolean allDependenciesPresent) {
        return allDependenciesPresent;
    }

    public boolean isModuleEnabled(String moduleName);

    @Nullable
    default public ForgeConfigSpec.BooleanValue getModuleConfig(String moduleName) {
        return null;
    }

    @Nullable
    default public IModuleContainer wrap(@Nonnull IModuleInfo module, BiFunction<Object, IModuleInfo, IModuleContainer> defaultWrapper) throws Exception {
        return defaultWrapper.apply(Class.forName(module.getModuleClass()).newInstance(), module);
    }

    default public void registerAdditionalModules(Consumer<IModuleInfo> registry) {
    }


}
