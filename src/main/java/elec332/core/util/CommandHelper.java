package elec332.core.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.registry.SimpleRegistries;
import elec332.core.handler.ModEventHandler;
import elec332.core.server.ServerHelper;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 16-10-2016.
 */
public class CommandHelper {

    public static ISingleObjectRegistry<ICommand> getServerCommandRegistry(){
        return serverCommandRegistry;
    }

    public static ISingleObjectRegistry<ICommand> getClientCommandRegistry(){
        return clientCommandRegistry;
    }

    private static final ISingleObjectRegistry<ICommand> serverCommandRegistry, clientCommandRegistry;

    static {
        if (FMLCommonHandler.instance().getSide().isClient()){
            clientCommandRegistry = new ISingleObjectRegistry<ICommand>() {

                @Override
                public boolean register(ICommand command) {
                    ClientCommandHandler.instance.registerCommand(command);
                    return true;
                }

                @Override
                public Set<ICommand> getAllRegisteredObjects() {
                    return Sets.newHashSet(ClientCommandHandler.instance.getCommands().values());
                }

            };
        } else {
            clientCommandRegistry = SimpleRegistries.emptySingleObjectRegistry();
        }
        final List<ICommand> commands = Lists.newArrayList();
        serverCommandRegistry = new ISingleObjectRegistry<ICommand>() {

            @Override
            public boolean register(ICommand command) {
                commands.add(command);
                return true;
            }

            @Override
            public Set<ICommand> getAllRegisteredObjects() {
                MinecraftServer server = ServerHelper.instance.getMinecraftServer();
                return server == null ? ImmutableSet.of() : Sets.newHashSet(server.commandManager.getCommands().values());
            }

        };

        ModEventHandler.registerCallback(new ModEventHandler.Callback() {
            @Override
            public void onEvent(FMLStateEvent event) {
                if (event instanceof FMLServerStartingEvent){
                    for (ICommand command : commands){
                        ((FMLServerStartingEvent) event).registerServerCommand(command);
                    }
                }
            }
        });
    }

}
