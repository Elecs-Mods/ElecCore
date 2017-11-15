package elec332.core.api;

import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.util.IDependencyHandler;
import net.minecraft.command.ICommand;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 20-10-2016.
 */
public interface IElecCoreMod {

    default public void registerRegisters(Consumer<IObjectRegister<?>> handler){
    }

    default public void registerServerCommands(ISingleRegister<ICommand> commandRegistry){
    }

    default public void registerClientCommands(ISingleRegister<ICommand> commandRegistry){
    }

    default public void registerSaveHandlers(ISingleRegister<IExternalSaveHandler> saveHandlerRegistry){
    }

    default public boolean useLangCompat(){
        return true;
    }

    default public IDependencyHandler getDependencyHandler(){
        return this instanceof IDependencyHandler ? (IDependencyHandler) this : null;
    }

}
