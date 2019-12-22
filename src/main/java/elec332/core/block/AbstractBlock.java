package elec332.core.block;

import com.google.common.base.Preconditions;
import elec332.core.tile.ITileWithDrops;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block implements IAbstractBlock {

    public AbstractBlock(Properties builder) {
        super(builder);
    }

    private String unlocalizedName;

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
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        return BlockMethods.onBlockActivated(state, world, pos, player, hand, hit, this);
    }

    @Override
    public boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest, IFluidState fluid) {
        return BlockMethods.removedByPlayer(state, world, pos, player, willHarvest, fluid, this);
    }

    @Nullable
    @Override //Old collisionRayTrace
    public RayTraceResult getRayTraceResult(BlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original) {
        return BlockMethods.collisionRayTrace(state, world, pos, start, end, this);
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        List<ItemStack> f = getOriginalDrops(state, builder);
        Entity entity = Preconditions.checkNotNull(builder.get(LootParameters.THIS_ENTITY));
        BlockPos pos = Preconditions.checkNotNull(builder.get(LootParameters.POSITION));
        ItemStack stack = Preconditions.checkNotNull(builder.get(LootParameters.TOOL));
        getTileDrops(f, entity.world, pos, EnchantmentHelper.getEnchantmentLevel(Enchantments.FORTUNE, stack));
        return f;
    }

    public List<ItemStack> getOriginalDrops(BlockState state, LootContext.Builder builder) {
        return super.getDrops(state, builder);
    }

    public void getTileDrops(List<ItemStack> drops, World world, BlockPos pos, int fortune) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof ITileWithDrops) {
            ((ITileWithDrops) tile).getDrops(drops, fortune);
        }
    }

    /*
    //@Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        BlockMethods.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, isActualState, this);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    //@Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(BlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return BlockMethods.getSelectedBoundingBox(state, world, pos, this);
    }
    */

}
