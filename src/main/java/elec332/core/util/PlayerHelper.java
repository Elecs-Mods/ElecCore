package elec332.core.util;

import com.google.common.base.Preconditions;
import elec332.core.util.math.RayTraceHelper;
import net.minecraft.Util;
import net.minecraft.commands.CommandSource;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import java.util.UUID;

/**
 * Created by Elec332 on 19-3-2015.
 */
public class PlayerHelper {

    /**
     * Gets the player's current block reach distance
     *
     * @param player The player
     * @return The player's current block reach distance
     */
    public static double getBlockReachDistance(Player player) {
        return Preconditions.checkNotNull(player.getAttribute(net.minecraftforge.common.ForgeMod.REACH_DISTANCE.get())).getValue();
    }

    /**
     * Gets the player's corrected eye position
     *
     * @param player The player
     * @return The corrected eye position
     */
    public static Vec3 getCorrectedEyePosition(Player player) {
        double yCoord = player.getY();
        if (player.getLevel().isClientSide()) {
            yCoord += player.getEyeHeight();// - player.getDefaultEyeHeight();
        } else {
            yCoord += player.getEyeHeight();
            if (player instanceof ServerPlayer && player.isCrouching()) {
                yCoord -= 0.08D;
            }
        }
        return new Vec3(player.getX(), yCoord, player.getZ());
    }

    /**
     * Gets a player's {@link UUID}
     *
     * @param player The player
     * @return The specified player's UUID
     */
    public static UUID getPlayerUUID(Player player) {
        return player.getGameProfile().getId();
    }

    /**
     * Sends a message to the specified player
     *
     * @param player The player
     * @param s      The message
     */
    public static void sendMessageToPlayer(@Nonnull CommandSource player, String s) {
        sendMessageToPlayer(player, new TextComponent(s));
    }

    /**
     * Sends a message to the specified player
     *
     * @param player The player
     * @param s      The message
     */
    public static void sendMessageToPlayer(@Nonnull CommandSource player, TextComponent s) {
        player.sendMessage(s, Util.NIL_UUID);
    }

    /**
     * Activates creative flight (only flight) for the specified player
     *
     * @param player The player
     */
    public static void activateFlight(Player player) {
        player.getAbilities().mayfly = true;
        player.onUpdateAbilities();
    }

    /**
     * De-activates creative flight for the specified player
     *
     * @param player The player
     */
    public static void deactivateFlight(Player player) {
        player.getAbilities().mayfly = false;
        if (player.getAbilities().flying) {
            player.getAbilities().flying = false;
        }
        player.onUpdateAbilities();
    }

    /**
     * Insta-kills an entity without glitches, with the specified player as attacker.
     *
     * @param attacker The player smiting the target
     * @param target   The entity that will be dead very soon
     */
    public static void smiteEntity(Player attacker, LivingEntity target) {
        EntityHelper.smiteEntity(DamageSource.playerAttack(attacker), target);
    }

    /**
     * Whether the specified player is in creative
     *
     * @param player The player
     * @return Whether the specified player is in creative
     */
    public static boolean isPlayerInCreative(Player player) {
        return player.isCreative();
    }

    /**
     * Perform a raytrace with a specified maximum distance
     *
     * @param player The player from which to start the raytracing
     * @param range  The raytracing range
     * @return The {@link HitResult} from the raytrace
     */
    public static HitResult getPosPlayerIsLookingAt(Player player, double range) {
        return RayTraceHelper.rayTrace(player, range);
    }

    /**
     * Checks if the 2 players are the same
     *
     * @param player1 Player 1
     * @param player2 Player 2
     * @return Whether Player 1 and Player 2 are the same player
     */
    public static boolean arePlayersEqual(Player player1, Player player2) {
        return player1.getUUID() == player2.getUUID();
    }

}
