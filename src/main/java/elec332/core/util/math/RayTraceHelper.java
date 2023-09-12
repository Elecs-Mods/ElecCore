package elec332.core.util.math;

import elec332.core.util.PlayerHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
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
     * @return The {@link BlockHitResult} from the raytrace
     */
    @Nullable
    public static BlockHitResult retraceBlock(Level world, BlockPos pos, Player player) {
        return retraceBlock(WorldHelper.getBlockState(world, pos), world, pos, player);
    }

    public static Vec3 slightExpand(Vec3 start, Vec3 end) {
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
    public static Pair<Vec3, Vec3> getRayTraceVectors(Player player) {
        Vec3 headVec = PlayerHelper.getCorrectedEyePosition(player);
        Vec3 lookVec = player.getLookAngle();
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3 endVec = headVec.add(lookVec.x * reach, lookVec.y * reach, lookVec.z * reach);
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
    public static BlockHitResult retraceBlock(BlockState blockState, Level world, BlockPos pos, Player player) {
        Pair<Vec3, Vec3> rayTraceVectors = getRayTraceVectors(player);
        return blockState.getShape(world, pos).clip(rayTraceVectors.getLeft(), rayTraceVectors.getRight(), pos);
    }

    /**
     * Perform a raytrace with a specified maximum distance
     *
     * @param player   The player from which to start the raytracing
     * @param distance The maximum raytracing distance
     * @return The {@link BlockHitResult} from the raytrace
     */
    public static BlockHitResult rayTrace(LivingEntity player, double distance) {
        Vec3 vec3d = new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
        Vec3 vec3d1 = getVectorForRotation(player.getXRot(), player.getYRot());
        Vec3 vec3d2 = vec3d.add(vec3d1.x * distance, vec3d1.y * distance, vec3d1.z * distance);
        ClipContext rtc = new ClipContext(vec3d, vec3d2, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return player.getLevel().clip(rtc);
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
    public static HitResult rayTrace(BlockPos pos, Vec3 start, Vec3 end, AABB boundingBox) {
        Vec3 vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vec3 vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        return AABB.clip(Collections.singleton(boundingBox), vec3d, vec3d1, pos);

        //RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        //return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }

    //Because this is protected in Entity -_-
    public static Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = Mth.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = Mth.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -Mth.cos(-pitch * 0.017453292F);
        float f3 = Mth.sin(-pitch * 0.017453292F);
        return new Vec3((double) (f1 * f2), (double) f3, (double) (f * f2));
    }

}
