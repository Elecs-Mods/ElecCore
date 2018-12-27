package elec332.core.util;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.registry.SimpleRegistries;
import net.minecraft.command.ICommand;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 16-10-2016.
 */
public class CommandHelper {

    /**
     * @return A registry for all server commands, removes the need to regster it everytime a server starts up
     */
    public static ISingleObjectRegistry<ICommand> getServerCommandRegistry() {
        return serverCommandRegistry;
    }

    /**
     * @return A registry for all client commands
     */
    public static ISingleObjectRegistry<ICommand> getClientCommandRegistry() {
        return clientCommandRegistry;
    }

    /**
     * INTERNAL USE ONLY!
     */
    public static void registerCommands(FMLServerStartingEvent event) {
        for (ICommand command : commands) {
            event.registerServerCommand(command);
        }
    }

    private static final ISingleObjectRegistry<ICommand> serverCommandRegistry, clientCommandRegistry;
    private static final List<ICommand> commands = Lists.newArrayList();

    static {
        if (FMLCommonHandler.instance().getSide().isClient()) {
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

        serverCommandRegistry = new ISingleObjectRegistry<ICommand>() {

            @Override
            public boolean register(ICommand command) {
                commands.add(command);
                return true;
            }

            @Override
            public Set<ICommand> getAllRegisteredObjects() {
                MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
                return server == null ? ImmutableSet.of() : Sets.newHashSet(server.commandManager.getCommands().values());
            }

        };

    }

}
