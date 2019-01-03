package elec332.core.util;

import com.google.common.collect.Sets;
import elec332.core.MC113ToDoReference;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.IElecCoreModHandler;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.Set;

/**
 * Created by Elec332 on 16-10-2016.
 */
public class CommandHelper {

    /**
     * @return A registry for all server commands, removes the need to regster it everytime a server starts up
     * <p>
     * public static ISingleObjectRegistry<ICommand> getServerCommandRegistry() {
     * return serverCommandRegistry;
     * }
     * <p>
     * /**
     * @return A registry for all client commands
     * <p>
     * public static ISingleObjectRegistry<ICommand> getClientCommandRegistry() {
     * return clientCommandRegistry;
     * }
     * <p>
     * /**
     * INTERNAL USE ONLY!
     */

    private static final Set<IElecCoreMod> mods = Sets.newHashSet();

    public static void registerCommands(FMLServerStartingEvent event) {
        mods.forEach(m -> m.registerCommands(event.getCommandDispatcher()));
    }

    @APIHandlerInject
    private void getCommandThings(IElecCoreModHandler handler) {
        handler.registerModHandler((mc, mod) -> mods.add(mod));
    }

    //private static final ISingleObjectRegistry<ICommand> serverCommandRegistry, clientCommandRegistry;
    //private static final List<ICommand> commands = Lists.newArrayList();

    static {
        MC113ToDoReference.update(); //Cleanup when client commands are implemented
        /*if (FMLCommonHandler.instance().getSide().isClient()) {
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

        };*/

    }

}
