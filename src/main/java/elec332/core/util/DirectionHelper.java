package elec332.core.util;

import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public static ModelRotation getRotationFromFacing(EnumFacing facing){
        switch (facing){
            case EAST:
                return ModelRotation.X0_Y270;
            case SOUTH:
                return ModelRotation.X0_Y180;
            case WEST:
                return ModelRotation.X0_Y90;
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
