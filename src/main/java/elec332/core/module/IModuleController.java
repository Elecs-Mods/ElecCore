package elec332.core.module;

import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleController {

    public static List<IModuleContainer> EMPTY_LIST = ImmutableList.of();

    public boolean isModuleEnabled(String moduleName);

    @Nullable
    default public Object invoke(@Nonnull Class<?> clazz) throws Exception {
        return clazz.newInstance();
    }

    @Nullable
    default public IModuleContainer wrap(@Nonnull Object module){
        if (!module.getClass().isAnnotationPresent(ElecModule.class)){
            return null;
        }
        return new WrappedModule(module, module.getClass().getAnnotation(ElecModule.class));
    }

    default public void registerAdditionalModules(Consumer<IModuleContainer> registry){
    }

}
