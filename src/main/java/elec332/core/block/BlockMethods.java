package elec332.core.block;

import elec332.core.util.PlayerHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.Hand;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 29-12-2018
 */
public final class BlockMethods {

    @SuppressWarnings("all")
    public static <B extends Block & IAbstractBlock> String createUnlocalizedName(B block) {
        return "tile." + block.getRegistryName().toString().replace(":", ".").toLowerCase();
    }

    public static <B extends Block & IAbstractBlock> boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit, B block) {
        return hit != null && block.onBlockActivated(world, pos, state, player, hand, hit);
    }

    public static <B extends Block & IAbstractBlock> boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest, IFluidState fluid, B block) {
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
