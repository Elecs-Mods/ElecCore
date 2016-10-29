package elec332.core.api.module;

import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created by Elec332 on 14-10-2016.
 */
public interface IModuleManager {

    @Nonnull
    public Set<IModuleContainer> getActiveModules();

    @Nullable
    public IModuleContainer getActiveModule(ResourceLocation module);

    public void invokeEvent(Object event);

}
