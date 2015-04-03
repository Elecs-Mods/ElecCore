package elec332.core.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 2-4-2015.
 */
public class DirectionHelper {
    public static ForgeDirection getFacingOnPlacement(EntityLivingBase entityLivingBase){
        switch (getDirectionNumberOnPlacement(entityLivingBase)){
            case 0:
                return ForgeDirection.NORTH;
            case 1:
                return ForgeDirection.EAST;
            case 2:
                return ForgeDirection.SOUTH;
            case 3:
                return ForgeDirection.WEST;
            default:
                return ForgeDirection.UNKNOWN;
        }
    }

    @Deprecated
    public static ForgeDirection getOppositeSide(ForgeDirection direction){
        switch (direction){
            case SOUTH:
                return ForgeDirection.NORTH;
            case WEST:
                return ForgeDirection.EAST;
            case NORTH:
                return ForgeDirection.SOUTH;
            case EAST:
                return ForgeDirection.WEST;
            case UP:
                return ForgeDirection.DOWN;
            case DOWN:
                return ForgeDirection.UP;
            default:
                return ForgeDirection.UNKNOWN;
        }
    }

    public static final int[][] ROTATION_MATRIX_YAW = {
            {0, 1, 2, 3, 4, 5},
            {0, 1, 4, 5, 3, 2},
            {0, 1, 3, 2, 5, 4},
            {0, 1, 5, 4, 2, 3},
    };

    public static int getDirectionNumberOnPlacement(EntityLivingBase entityLivingBase){
        return MathHelper.floor_double((double) (entityLivingBase.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
    }
}
