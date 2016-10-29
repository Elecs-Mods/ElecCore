package elec332.core.module;

import elec332.core.api.module.IModuleContainer;
import elec332.core.api.module.IModuleHandler;
import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.network.impl.NetworkManager;
import elec332.core.util.CommandHelper;
import net.minecraft.command.ICommand;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 15-10-2016.
 */
public class DefaultModuleHandler implements IModuleHandler {

    public DefaultModuleHandler(IModuleContainer module){
        this.module = module;
    }

    private final IModuleContainer module;
    private ISimpleNetworkPacketManager packetManager;

    @Override
    public Logger getLogger() {
        return LogManager.getLogger(module.getCombinedName().toString());
    }

    @Override
    public ISimpleNetworkPacketManager getPacketHandler() {
        if (packetManager == null){
            packetManager = NetworkManager.INSTANCE.getAdditionalSimpleNetworkManager(module.getOwnerMod(), module.getName());
        }
        return packetManager;
    }

    @Override
    public ISingleObjectRegistry<ICommand> getClientCommandRegistry() {
        return CommandHelper.getClientCommandRegistry();
    }

    @Override
    public ISingleObjectRegistry<ICommand> getServerCommandRegistry() {
        return CommandHelper.getServerCommandRegistry();
    }

}
