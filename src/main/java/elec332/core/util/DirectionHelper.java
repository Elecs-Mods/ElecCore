package elec332.core.util;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 2-4-2015.
 */
public class DirectionHelper {

    public static ForgeDirection getFacingOnPlacement(EntityLivingBase entityLivingBase){
        return getDirectionFromNumber(getDirectionNumberOnPlacement(entityLivingBase));
    }

    public static int getNumberForDirection(ForgeDirection forgeDirection){
        switch (forgeDirection){
            case NORTH:
                return 0;
            case EAST:
                return 1;
            case SOUTH:
                return 2;
            case WEST:
                return 3;
            default:
                return -1;
        }
    }

    public static ForgeDirection getDirectionFromNumber(int i){
        switch (i){
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

    public static ForgeDirection rotateLeft(ForgeDirection direction){
        switch (direction){
            case NORTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.NORTH;
            default:
                return direction;
        }
    }

    public static ForgeDirection rotateRight(ForgeDirection direction){
        switch (direction){
            case NORTH:
                return ForgeDirection.WEST;
            case WEST:
                return ForgeDirection.SOUTH;
            case SOUTH:
                return ForgeDirection.EAST;
            case EAST:
                return ForgeDirection.NORTH;
            default:
                return direction;
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

    public static void setFacing_YAW(World world, int i, int i1, int i2, ForgeDirection forgeDirection) {
        world.markBlockForUpdate(i, i1, i2);
        world.setBlockMetadataWithNotify(i, i1, i2, getNumberForDirection(forgeDirection), 2);
    }
}
