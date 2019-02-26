package elec332.core.block;

import com.google.common.collect.Lists;
import elec332.core.util.IndexedAABB;
import elec332.core.util.PlayerHelper;
import elec332.core.util.RayTraceHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 29-12-2018
 */
public final class BlockMethods {

    @SuppressWarnings("all")
    public static <B extends Block & IAbstractBlock> String createUnlocalizedName(B block) {
        return "tile." + block.getRegistryName().toString().replace(":", ".").toLowerCase();
    }

    public static <B extends Block & IAbstractBlock> RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end, B block) {
        List<AxisAlignedBB> boxes = Lists.newArrayList();
        block.addSelectionBoxes(state, world, pos, boxes);
        return boxes.stream().reduce(null, (prev, box) -> {
            RayTraceResult hit = RayTraceHelper.rayTrace(pos, start, end, box);
            if (hit != null && box instanceof IndexedAABB) {
                hit.subHit = ((IndexedAABB) box).index;
            }
            return prev != null && (hit == null || start.squareDistanceTo(prev.hitVec) < start.squareDistanceTo(hit.hitVec)) ? prev : hit;
        }, (a, b) -> b);
    }

    public static <B extends Block & IAbstractBlock> boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ, B block) {
        RayTraceResult hit = RayTraceHelper.retraceBlock(state, world, pos, player);
        return hit != null && block.onBlockActivated(world, pos, state, player, hand, hit);
    }

    public static <B extends Block & IAbstractBlock> boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest, IFluidState fluid, B block) {
        if (!block.canBreak(world, pos, player)) {
            if (PlayerHelper.isPlayerInCreative(player)) {
                block.onBlockClicked(state, world, pos, player);
            }
            return false;
        }
        block.onBlockHarvested(world, pos, state, player);
        return world.setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
    }

}
