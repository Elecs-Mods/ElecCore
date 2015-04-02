package elec332.core.player;

import elec332.core.main.ElecCore;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;

/**
 * Created by Elec332 on 19-3-2015.
 */
public class PlayerHelper {

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
