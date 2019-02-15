package elec332.core.util;

import net.minecraft.command.ICommandSource;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

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
    public static double getBlockReachDistance(EntityPlayer player) {
        return player.getAttribute(EntityPlayer.REACH_DISTANCE).getValue();
    }

    /**
     * Gets the player's corrected head vector
     *
     * @param player The player
     * @return The corrected head vector
     */
    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {
        double yCoord = player.posY;
        if (player.getEntityWorld().isRemote) {
            yCoord += player.getEyeHeight();// - player.getDefaultEyeHeight();
        } else {
            yCoord += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                yCoord -= 0.08D;
            }
        }
        return new Vec3d(player.posX, yCoord, player.posZ);
    }

    /**
     * Gets a player's {@link UUID}
     *
     * @param player The player
     * @return The specified player's UUID
     */
    public static UUID getPlayerUUID(EntityPlayer player) {
        return player.getGameProfile().getId();
    }

    /**
     * Sends a message to the specified player
     *
     * @param player The player
     * @param s      The message
     */
    public static void sendMessageToPlayer(@Nonnull ICommandSource player, String s) {
        sendMessageToPlayer(player, new TextComponentString(s));
    }

    /**
     * Sends a message to the specified player
     *
     * @param player The player
     * @param s      The message
     */
    public static void sendMessageToPlayer(@Nonnull ICommandSource player, ITextComponent s) {
        player.sendMessage(s);
    }

    /**
     * Activates creative flight (only flight) for the specified player
     *
     * @param player The player
     */
    public static void activateFlight(EntityPlayer player) {
        player.abilities.allowFlying = true;
        player.sendPlayerAbilities();
    }

    /**
     * De-activates creative flight for the specified player
     *
     * @param player The player
     */
    public static void deactivateFlight(EntityPlayer player) {
        player.abilities.allowFlying = false;
        if (player.abilities.isFlying) {
            player.abilities.isFlying = false;
        }
        player.sendPlayerAbilities();
    }

    /**
     * Insta-kills an entity without glitches, with the specified player as attacker.
     *
     * @param attacker The player smiting the target
     * @param target   The entity that will be dead very soon
     */
    public static void smiteEntity(EntityPlayer attacker, EntityLivingBase target) {
        EntityHelper.smiteEntity(DamageSource.causePlayerDamage(attacker), target);
    }

    /**
     * Whether the specified player is in creative
     *
     * @param player The player
     * @return Whether the specified player is in creative
     */
    public static boolean isPlayerInCreative(EntityPlayer player) {
        return player.abilities.isCreativeMode;
    }

    /**
     * Perform a raytrace with a specified maximum distance
     *
     * @param player The player from which to start the raytracing
     * @param range  The raytracing range
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static RayTraceResult getPosPlayerIsLookingAt(EntityPlayer player, double range) {
        return RayTraceHelper.rayTrace(player, range);
    }

    /**
     * Checks if the 2 players are the same
     *
     * @param player1 Player 1
     * @param player2 Player 2
     * @return Whether Player 1 and Player 2 are the same player
     */
    public static boolean arePlayersEqual(EntityPlayer player1, EntityPlayer player2) {
        return player1.getUniqueID() == player2.getUniqueID();
    }

}
