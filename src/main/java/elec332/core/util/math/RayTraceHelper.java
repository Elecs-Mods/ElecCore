package elec332.core.util.math;

import com.google.common.base.Preconditions;
import elec332.core.util.PlayerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;

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
    public static BlockRayTraceResult retraceBlock(World world, BlockPos pos, PlayerEntity player) {
        return retraceBlock(WorldHelper.getBlockState(world, pos), world, pos, player);
    }

    public static Vector3d slightExpand(Vector3d start, Vector3d end) {
        return end.add(end.subtract(start).normalize().scale(0.002d));
    }

    /**
     * Returns the player's head and the end of the player's look vector,
     * to be used as the "head" or "start" and "end" vectors in raytracing calculations respectively;
     *
     * @param player The player to calculate the vectors from
     * @return The player's raytrace vectors
     */
    @Nonnull
    public static Pair<Vector3d, Vector3d> getRayTraceVectors(PlayerEntity player) {
        //player.getLookVec()
        //Vector3d startPos = new Vector3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
        //Vector3d endPos = startPos.add(new Vector3d(entity.getLookVec().x * length, entity.getLookVec().y * length, entity.getLookVec().z * length));


        Vector3d headVec = PlayerHelper.getCorrectedEyePosition(player);
        Vector3d lookVec = player.getLook(0);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vector3d endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
        return Pair.of(headVec, endVec);
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
    public static BlockRayTraceResult retraceBlock(BlockState blockState, World world, BlockPos pos, PlayerEntity player) {
        Pair<Vector3d, Vector3d> rayTraceVectors = getRayTraceVectors(player);
        return blockState.getShape(world, pos).rayTrace(rayTraceVectors.getLeft(), rayTraceVectors.getRight(), pos);
        //Old
        //return Block.collisionRayTrace(blockState, world, pos, rayTraceVectors.getLeft(), rayTraceVectors.getRight());
    }

    /**
     * Perform a raytrace from a specified position to another position
     *
     * @param world The world
     * @param start The position from which to start the raytracing
     * @param end   The position to raytrace to
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static BlockRayTraceResult rayTrace(IBlockReader world, BlockPos start, BlockPos end) {
        return rayTrace(world, Vector3d.copy(start), end);
    }

    /**
     * Perform a raytrace from a specified position to another position
     *
     * @param world The world
     * @param start The position from which to start the raytracing
     * @param end   The position to raytrace to
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static BlockRayTraceResult rayTrace(IBlockReader world, Vector3d start, BlockPos end) {
        return rayTrace(world, start, Vector3d.copy(end));
    }

    /**
     * Perform a raytrace from a specified position to another position
     *
     * @param world The world
     * @param start The position from which to start the raytracing
     * @param end   The position to raytrace to
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static BlockRayTraceResult rayTrace(IBlockReader world, Vector3d start, Vector3d end) {
        RayTraceContext rtc = new RayTraceContext(start, end, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, null);
        return Preconditions.checkNotNull(world).rayTraceBlocks(rtc);
    }

    /**
     * Perform a raytrace with a specified maximum distance
     *
     * @param player   The player from which to start the raytracing
     * @param distance The maximum raytracing distance
     * @return The {@link RayTraceResult} from the raytrace
     */
    public static BlockRayTraceResult rayTrace(LivingEntity player, double distance) {
        Vector3d Vector3d = new Vector3d(player.getPosX(), player.getPosY() + player.getEyeHeight(), player.getPosZ());
        Vector3d Vector3d1 = getVectorForRotation(player.rotationPitch, player.rotationYawHead);
        Vector3d Vector3d2 = Vector3d.add(Vector3d1.x * distance, Vector3d1.y * distance, Vector3d1.z * distance);
        RayTraceContext rtc = new RayTraceContext(Vector3d, Vector3d2, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player);
        return player.getEntityWorld().rayTraceBlocks(rtc);
        //return player.getEntityWorld().rayTraceBlocks(Vector3d, Vector3d2, RayTraceFluidMode.NEVER, false, true);
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
    public static RayTraceResult rayTrace(BlockPos pos, Vector3d start, Vector3d end, AxisAlignedBB boundingBox) {
        Vector3d Vector3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vector3d Vector3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        return AxisAlignedBB.rayTrace(Collections.singleton(boundingBox), Vector3d, Vector3d1, pos);

        //RayTraceResult raytraceresult = boundingBox.calculateIntercept(Vector3d, Vector3d1);
        //return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }

    //Because this is protected in Entity -_-
    public static Vector3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vector3d((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

}
