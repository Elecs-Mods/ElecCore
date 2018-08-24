package elec332.core.util;

import net.minecraft.command.ICommandSender;
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

    public static double getBlockReachDistance(EntityPlayer player) {
        return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue();
    }

    public static Vec3d getCorrectedHeadVec(EntityPlayer player) {
        double yCoord = player.posY;
        if (player.getEntityWorld().isRemote) {
            yCoord += (player.getEyeHeight() - player.getDefaultEyeHeight());
        } else {
            yCoord += player.getEyeHeight();
            if (player instanceof EntityPlayerMP && player.isSneaking()) {
                yCoord -= 0.08D;
            }
        }
        return new Vec3d(player.posX, yCoord, player.posZ);
    }

    public static UUID getPlayerUUID(EntityPlayer player) {
        return player.getGameProfile().getId();
    }

    public static void sendMessageToPlayer(@Nonnull ICommandSender player, String s) {
        sendMessageToPlayer(player, new TextComponentString(s));
    }

    public static void sendMessageToPlayer(@Nonnull ICommandSender player, ITextComponent s) {
        player.sendMessage(s);
    }

    public static void activateFlight(EntityPlayer player) {
        player.capabilities.allowFlying = true;
        player.sendPlayerAbilities();
    }

    public static void deactivateFlight(EntityPlayer player) {
        player.capabilities.allowFlying = false;
        if (player.capabilities.isFlying) {
            player.capabilities.isFlying = false;
        }
        player.sendPlayerAbilities();
    }

    public static void smiteEntity(EntityPlayer attacker, EntityLivingBase target) {
        EntityHelper.smiteEntity(DamageSource.causePlayerDamage(attacker), target);
    }

    public static boolean isPlayerInCreative(EntityPlayer player) {
        return player.capabilities.isCreativeMode;
    }

    public static RayTraceResult getPosPlayerIsLookingAt(EntityPlayer player, double range) {
        return RayTraceHelper.rayTrace(player, range);
    }

    public static boolean arePlayersEqual(EntityPlayer player1, EntityPlayer player2) {
        return player1.getUniqueID() == player2.getUniqueID();
    }

}
