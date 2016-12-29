package elec332.core.util;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 29-12-2016.
 */
public abstract class AbstractCommandBase extends CommandBase {

    @Nonnull
    @Override
    public final String getCommandName() {
        return getMCCommandName();
    }

    @Nonnull
    @Override
    public final String getCommandUsage(ICommandSender sender) {
        return getMCCommandUsage(sender);
    }

    @Nonnull
    @Override
    public final List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return getMCTabCompletions(server, sender, args, pos);
    }

    @Nonnull
    public abstract String getMCCommandName();

    @Nonnull
    public abstract String getMCCommandUsage(ICommandSender sender);

    @Nonnull
    public List<String> getMCTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos pos) {
        return super.getTabCompletionOptions(server, sender, args, pos);
    }

}
