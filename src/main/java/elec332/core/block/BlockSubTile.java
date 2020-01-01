package elec332.core.block;

import com.google.common.collect.Lists;
import elec332.core.tile.sub.ISubTileLogic;
import elec332.core.tile.sub.TileMultiObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 20-2-2018
 */
public class BlockSubTile extends AbstractBlock implements ISelectionBoxOverride {

    @SuppressWarnings("all")
    public BlockSubTile(Properties builder, Class<? extends ISubTileLogic>... subtiles) {
        super(builder.hardnessAndResistance(5));
        this.subtiles = subtiles;
    }

    private final Class<? extends ISubTileLogic>[] subtiles;

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileMultiObject(subtiles);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext selectionContext) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        if (tile == null) { //tile is null when checking placement
            return VoxelShapes.empty();
        }
        return tile.getShape(state, pos);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public VoxelShape getSelectionBox(BlockState state, IWorld world, BlockPos pos, PlayerEntity player, RayTraceResult hit) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        return tile.getSelectionBox(state, player, hit);
    }

    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onReplaced(@Nonnull BlockState me, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        tile.onRemoved();
        super.onReplaced(me, world, pos, newState, isMoving);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        return tile.removedByPlayer(state, player, willHarvest, fluid, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighbor, BlockPos p_189540_5_, boolean isMoving) {
        neighborChanged(world, pos, p_189540_5_, false, neighbor);
    }

    @Override
    public void observedNeighborChange(BlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos) {
        neighborChanged(world, observerPos, changedBlockPos, true, changedBlock);
    }

    private void neighborChanged(World world, BlockPos pos, BlockPos neighborPos, boolean observer, Block changedBlock) {
        if (!world.isRemote) {
            TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
            if (!tile.shouldRefresh(world.getGameTime(), neighborPos) && observer) {
                return;
            }
            tile.neighborChanged(neighborPos, observer, world.getFluidState(pos), changedBlock);
        }
    }

    @Override
    public List<ItemStack> getDrops(List<ItemStack> drops, @Nonnull LootContext.Builder builder, Entity entity, World world, BlockPos pos, @Nonnull BlockState state, ItemStack stack) {
        return Lists.newArrayList();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        return tile.onBlockActivated(player, hand, state, hit);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileMultiObject tile = getTileEntity(world, pos, TileMultiObject.class);
        return tile.getStack(target, player);
    }

}
