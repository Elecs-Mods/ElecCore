package elec332.core.block;

import com.google.common.base.Preconditions;
import elec332.core.tile.IActivatableTile;
import elec332.core.tile.ITileWithDrops;
import elec332.core.util.BlockProperties;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block implements IAbstractBlock {

    public AbstractBlock(Properties builder) {
        super(builder);
        if (getDefaultState().has(BlockProperties.WATERLOGGED)) {
            setDefaultState(getDefaultState().with(BlockStateProperties.WATERLOGGED, false));
        }
    }

    private String unlocalizedName;

    public void setBlockRenderType(RenderType renderType) {
        RenderTypeLookup.setRenderLayer(this, renderType);
    }

    public void setBlockRenderType(Predicate<RenderType> renderTypes) {
        RenderTypeLookup.setRenderLayer(this, renderTypes);
    }

    @Nonnull
    @Override
    public String getTranslationKey() {
        if (this.unlocalizedName == null) {
            unlocalizedName = BlockMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    /**
     * Shape used for entity collision
     * (Usually redirects to {@link #getShape(BlockState, IBlockReader, BlockPos, ISelectionContext)}, returns empty shape when it doesn't block movement)
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext selectionContext) {
        return super.getCollisionShape(state, world, pos, selectionContext);
    }

    /**
     * Shape used for secondary raytracing.
     * If there was a valid {@link RayTraceResult} on {@link #getShape(BlockState, IBlockReader, BlockPos, ISelectionContext)},
     * then raytracing will also be performed on this shape, to maybe correct the {@link BlockRayTraceResult#getFace()}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getRaytraceShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return super.getRaytraceShape(state, world, pos);
    }

    /**
     * -Shape used for checking whether the block is opaque when {@link BlockState#isSolid()} is false
     * -Shape used for checking if a side of this block is solid (for rendering)
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getRenderShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return super.getRenderShape(state, world, pos);
    }

    /**
     * -Shape used in hitbox drawing on the client
     * -Shape used in primary collision raytracing
     * -Shape used in checking whether the block is opaque for the skybox (Only the skybox!)
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, ISelectionContext selectionContext) {
        return super.getShape(state, world, pos, selectionContext);
    }

    @Override
    @Deprecated
    @Nonnull
    @SuppressWarnings("deprecation")
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return BlockMethods.onBlockActivated(state, world, pos, player, hand, hit, this);
    }

    @Override
    @Deprecated
    public boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        return BlockMethods.removedByPlayer(state, world, pos, player, willHarvest, fluid, this);
    }

    @Nonnull
    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        Entity entity = builder.get(LootParameters.THIS_ENTITY);
        BlockPos pos = Preconditions.checkNotNull(builder.get(LootParameters.POSITION));
        ItemStack stack = Preconditions.checkNotNull(builder.get(LootParameters.TOOL));
        return getDrops(getOriginalDrops(state, builder), builder, entity, builder.getWorld(), pos, state, stack);
    }

    @Override
    public List<ItemStack> getDrops(List<ItemStack> drops, @Nonnull LootContext.Builder builder, @Nullable Entity entity, World world, BlockPos pos, @Nonnull BlockState state, ItemStack stack) {
        getTileDrops(drops, world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
        return drops;
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockState updatePostPlacement(@Nonnull BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.has(BlockProperties.WATERLOGGED) && stateIn.get(BlockStateProperties.WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IFluidState getFluidState(BlockState state) {
        if (state.has(BlockProperties.WATERLOGGED) && state.get(BlockStateProperties.WATERLOGGED)) {
            return Fluids.WATER.getStillFluidState(false);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, @Nonnull IBlockReader reader, @Nonnull BlockPos pos) {
        if (state.has(BlockProperties.WATERLOGGED) && state.get(BlockStateProperties.WATERLOGGED)) {
            return false;
        }
        return super.propagatesSkylightDown(state, reader, pos);
    }

    @Override
    protected void fillStateContainer(@Nonnull StateContainer.Builder<Block, BlockState> builder) {
        super.fillStateContainer(builder);
        if (this instanceof IWaterLoggable) {
            builder.add(BlockStateProperties.WATERLOGGED);
        }
    }

    @Override
    public ActionResultType onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        return tile instanceof IActivatableTile ? ((IActivatableTile) tile).onBlockActivated(player, hand, hit) : ActionResultType.PASS;
    }

    @SuppressWarnings("all")
    public final List<ItemStack> getOriginalDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    public final void getTileDrops(List<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof ITileWithDrops) {
            ((ITileWithDrops) tile).getDrops(drops, fortune);
        }
    }

}
