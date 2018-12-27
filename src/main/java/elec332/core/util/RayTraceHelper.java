package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 15-10-2015.
 */
public class RayTraceHelper {

    /**
     * Re-raytraces the provided position, to e.g. get more hit data than may be provided in some methods
     *
     * @param world  The world
     * @param player The player from which to start the raytracing
     * @param pos    The position to raytrace to
     * @return The {@link RayTraceResult} from the raytrace
     */
    @Nullable
    @SuppressWarnings("all")
    public static RayTraceResult retraceBlock(World world, EntityPlayer player, BlockPos pos) {
        IBlockState blockState = WorldHelper.getBlockState(world, pos);
        Vec3d headVec = PlayerHelper.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3d endVec = headVec.addVector(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return blockState.collisionRayTrace(world, pos, headVec, endVec);
    }

    /**
     * Perform a raytrace with a specified maximum distance
     *
     * @param player   The player from which to start the raytracing
     * @param distance The maximum raytracing distance
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static RayTraceResult rayTrace(EntityLivingBase player, double distance) {
        Vec3d vec3d = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d vec3d1 = getVectorForRotation(player.rotationPitch, player.rotationYawHead);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        return player.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

    //Because this is protected in Entity -_-
    public static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

}
