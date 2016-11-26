package elec332.core.api;

import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.util.IDependencyHandler;
import net.minecraft.command.ICommand;

/**
 * Created by Elec332 on 20-10-2016.
 */
public interface IElecCoreMod {

    default public void registerServerCommands(ISingleRegister<ICommand> commandregistry){
    }

    default public void registerClientCommands(ISingleRegister<ICommand> commandregistry){
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
