package elec332.core.api.mod;

import com.mojang.brigadier.CommandDispatcher;
import elec332.core.api.registration.IObjectRegister;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.storage.IExternalSaveHandler;
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

    default void registerRegisters(Consumer<IObjectRegister<?>> objectHandler, Consumer<IWorldGenRegister> worldHandler) {
    }

    default void registerCommands(CommandDispatcher<CommandSource> commandRegistry) {
    }

    default void registerClientCommands(CommandDispatcher<? extends ISuggestionProvider> commandRegistry) {
    }

    default void registerSaveHandlers(ISingleRegister<IExternalSaveHandler> saveHandlerRegistry) {
    }

    default void afterConstruction() {
    }

}
