package elec332.core.api;

import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.registry.ISingleRegister;
import net.minecraft.command.ICommand;

/**
 * Created by Elec332 on 20-10-2016.
 */
public interface IElecCoreMod {

    default public String getRequiredForgeVersion(){
        return null;
    }

    default public void registerServerCommands(ISingleRegister<ICommand> commandregistry){
    }

    default public void registerClientCommands(ISingleRegister<ICommand> commandregistry){
    }

    default public void registerSaveHandlers(ISingleRegister<IExternalSaveHandler> saveHandlerRegistry){
    }

}
