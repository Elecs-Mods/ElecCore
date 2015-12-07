package elec332.core.util;

import net.minecraft.client.resources.model.ModelRotation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 2-4-2015.
 */
public class DirectionHelper {

    public static EnumFacing getFacingOnPlacement(EntityLivingBase entityLivingBase){
        return getDirectionFromNumber(getDirectionNumberOnPlacement(entityLivingBase));
    }

    public static int getNumberForDirection(EnumFacing forgeDirection){
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

    public static EnumFacing getDirectionFromNumber(int i){
        switch (i){
            case 0:
                return EnumFacing.NORTH;
            case 1:
                return EnumFacing.EAST;
            case 2:
                return EnumFacing.SOUTH;
            case 3:
                return EnumFacing.WEST;
            default:
                return null;
        }
    }

    public static ModelRotation getRotationFromFacing(EnumFacing facing){
        switch (facing){
            case EAST:
                return ModelRotation.X0_Y90;
            case SOUTH:
                return ModelRotation.X0_Y180;
            case WEST:
                return ModelRotation.X0_Y270;
            default:
                return ModelRotation.X0_Y0;
        }
    }

    public static EnumFacing rotateLeft(EnumFacing direction){
        switch (direction){
            case NORTH:
                return EnumFacing.EAST;
            case EAST:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.NORTH;
            default:
                return direction;
        }
    }

    public static EnumFacing rotateRight(EnumFacing direction){
        switch (direction){
            case NORTH:
                return EnumFacing.WEST;
            case WEST:
                return EnumFacing.SOUTH;
            case SOUTH:
                return EnumFacing.EAST;
            case EAST:
                return EnumFacing.NORTH;
            default:
                return direction;
        }
    }

    @Deprecated
    public static EnumFacing getOppositeSide(EnumFacing direction){
        switch (direction){
            case SOUTH:
                return EnumFacing.NORTH;
            case WEST:
                return EnumFacing.EAST;
            case NORTH:
                return EnumFacing.SOUTH;
            case EAST:
                return EnumFacing.WEST;
            case UP:
                return EnumFacing.DOWN;
            case DOWN:
                return EnumFacing.UP;
            default:
                return null;
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

    @Deprecated
    public static void setFacing_YAW(World world, BlockPos blockPos, EnumFacing forgeDirection) {
        world.markBlockForUpdate(blockPos);
        //world.setBlockMetadataWithNotify(i, i1, i2, getNumberForDirection(forgeDirection), 2);
        throw new UnsupportedOperationException();
    }
}
