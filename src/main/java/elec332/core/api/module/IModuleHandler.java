package elec332.core.api.module;

import elec332.core.api.network.simple.ISimpleNetworkPacketManager;
import elec332.core.api.registry.ISingleObjectRegistry;
import net.minecraft.command.ICommand;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 15-10-2016.
 */
public interface IModuleHandler {

    public Logger getLogger();

    public ISimpleNetworkPacketManager getPacketHandler();

    public ISingleObjectRegistry<ICommand> getClientCommandRegistry();

    public ISingleObjectRegistry<ICommand> getServerCommandRegistry();

}
