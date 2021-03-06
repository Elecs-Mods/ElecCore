package elec332.core.api.module;

import net.minecraftforge.fml.ModContainer;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-9-2016.
 * <p>
 * Container for a module, somewhat like {@link ModContainer}
 */
public interface IModuleContainer extends IModuleInfo {

    @Nonnull
    Object getModule();

    @Nonnull
    ModContainer getOwnerMod();

    /**
     * Used for invoking events on the module.
     *
     * @param event The event
     * @throws Exception If the container failed to dispatch the event to the module,
     *                   or if the module itself threw an exception processing this event
     */
    void invokeEvent(Object event) throws Exception;

}
