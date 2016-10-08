package elec332.core.module;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-9-2016.
 */
public interface IModuleContainer extends IModuleInfo {

    @Nonnull
    public Object getModule();

    public void invokeEvent(Object event) throws Exception;

}
