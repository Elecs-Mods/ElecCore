package elec332.core.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;

/**
 * Created by Elec332 on 19-3-2015.
 */
public class PlayerHelper {

    public static MovingObjectPosition getPosPlayerIsLookingAt(EntityPlayer player, Double range){
        return player.rayTrace(range, 1.0F);
    }

    public static boolean arePlayersEqual(EntityPlayer player1, EntityPlayer player2){
        return player1.getUniqueID() == player2.getUniqueID();
    }
}
