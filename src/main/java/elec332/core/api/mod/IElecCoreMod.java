package elec332.core.api.mod;

import com.mojang.brigadier.CommandDispatcher;
import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.registry.ISingleRegister;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;

import java.util.function.Consumer;

/**
 * Created by Elec332 on 20-10-2016.
 * <p>
 * Can be implemented in main mod or module classes,
 * provides some extra features/helpers
 */
public interface IElecCoreMod {

    default public void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
    }

    default public void registerCommands(CommandDispatcher<CommandSource> commandRegistry) {
    }

    default public void registerClientCommands(CommandDispatcher<? extends ISuggestionProvider> commandRegistry) {
    }

    default public void registerSaveHandlers(ISingleRegister<IExternalSaveHandler> saveHandlerRegistry) {
    }

}
