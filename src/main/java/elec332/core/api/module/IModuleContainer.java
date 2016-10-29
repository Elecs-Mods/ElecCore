package elec332.core.api.module;

import net.minecraftforge.fml.common.ModContainer;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleContainer extends IModuleInfo {

    @Nonnull
    public Object getModule();

    @Nonnull
    public ModContainer getOwnerMod();

    public void invokeEvent(Object event) throws Exception;

    @Nonnull
    public IModuleHandler getHandler();

}
