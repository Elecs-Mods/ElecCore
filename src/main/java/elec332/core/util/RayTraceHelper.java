package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.*;
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
    public static RayTraceResult retraceBlock(World world, BlockPos pos, EntityPlayer player) {
        return retraceBlock(WorldHelper.getBlockState(world, pos), world, pos, player);
    }

    /**
     * Re-raytraces the provided position, to e.g. get more hit data than may be provided in some methods
     *
     * @param blockState The block being re-traced
     * @param world      The world
     * @param player     The player from which to start the raytracing
     * @param pos        The position to raytrace to
     * @return The {@link RayTraceResult} from the raytrace
     */
    @Nullable
    @SuppressWarnings("all")
    public static RayTraceResult retraceBlock(IBlockState blockState, World world, BlockPos pos, EntityPlayer player) {
        Vec3d headVec = PlayerHelper.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3d endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return Block.collisionRayTrace(blockState, world, pos, headVec, endVec);
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
        Vec3d vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        return player.getEntityWorld().rayTraceBlocks(vec3d, vec3d2, RayTraceFluidMode.NEVER, false, true);
    }

    /**
     * Raytraces to the provided AABB
     *
     * @param pos         The offset pos
     * @param start       The start position, calculated from {@param pos}
     * @param end         The end position, calculated from {@param pos}
     * @param boundingBox The AABB
     * @return The raytrace result, can be null
     */
    @Nullable
    public static RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
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
