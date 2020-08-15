package elec332.core.api.module;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleController {

    default boolean shouldModuleConstruct(IModuleInfo moduleInfo, boolean allDependenciesPresent) {
        return allDependenciesPresent;
    }

    boolean isModuleEnabled(String moduleName);

    @Nullable
    default ForgeConfigSpec.BooleanValue getModuleConfig(String moduleName) {
        return null;
    }

    @Nullable
    default IModuleContainer wrap(@Nonnull IModuleInfo module, Function<IModuleInfo, Object> invoker, BiFunction<Object, IModuleInfo, IModuleContainer> defaultWrapper) throws Exception {
        return defaultWrapper.apply(invoker.apply(module), module);
    }

    default void registerAdditionalModules(Consumer<IModuleInfo> registry, BiFunction<String, Class<?>, IModuleInfo.Builder> factory1, BiFunction<String, String, IModuleInfo.Builder> factory2) {
    }

}
