package elec332.core.player;

import elec332.core.main.ElecCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

import java.util.UUID;

/**
 * Created by Elec332 on 19-3-2015.
 */
public class PlayerHelper {

    public static double getBlockReachDistance(EntityPlayerMP player){
        return player.theItemInWorldManager.getBlockReachDistance();
    }

    public static Vec3 getCorrectedHeadVec(EntityPlayer player) {
        Vec3 v = Vec3.createVectorHelper(player.posX, player.posY, player.posZ);
        if(player.worldObj.isRemote) {
            v.yCoord += (player.getEyeHeight() - player.getDefaultEyeHeight());
        } else {
            v.yCoord += player.getEyeHeight();
            if(player instanceof EntityPlayerMP && player.isSneaking()) {
                v.yCoord -= 0.08D;
            }
        }
        return v;
    }

    public static UUID getPlayerUUID(EntityPlayer player){
        return player.getGameProfile().getId();
    }

    public static void sendMessageToPlayer(EntityPlayer player, String s){
        try {
            player.addChatComponentMessage(new ChatComponentText(s));
        } catch (NullPointerException e){
            //Null player, whoops
        }
    }

    public static void addPersonalMessageToClient(String s){
        ElecCore.proxy.addPersonalMessageToPlayer(s);
    }

    public static void activateFlight(EntityPlayer player){
        player.capabilities.allowFlying = true;
        player.sendPlayerAbilities();
    }

    public static void deactivateFlight(EntityPlayer player){
        player.capabilities.allowFlying = false;
        if (player.capabilities.isFlying)
            player.capabilities.isFlying = false;
        player.sendPlayerAbilities();
    }

    public static void smiteEntity(EntityPlayer attacker, EntityLivingBase target) {  //non-buggy version by InfinityRaider
        target.setHealth(0);
        target.onDeath(DamageSource.causePlayerDamage(attacker));
        target.setDead();
    }

    public static boolean isPlayerInCreative(EntityPlayer player){
        return player.capabilities.isCreativeMode;
    }

    public static MovingObjectPosition getPosPlayerIsLookingAt(EntityPlayer player, Double range){
        return player.rayTrace(range, 1.0F);
    }

    public static boolean arePlayersEqual(EntityPlayer player1, EntityPlayer player2){
        return player1.getUniqueID() == player2.getUniqueID();
    }
}
