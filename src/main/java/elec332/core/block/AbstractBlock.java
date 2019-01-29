package elec332.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block implements IAbstractBlock {

    public AbstractBlock(Builder builder) {
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
     * (Usually redirects to {@link #getShape(IBlockState, IBlockReader, BlockPos)}, returns empty shape when it doesn't block movement)
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getCollisionShape(@Nonnull IBlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return super.getCollisionShape(state, world, pos);
    }

    /**
     * Shape used for secondary raytracing.
     * If there was a valid {@link RayTraceResult} on {@link #getShape(IBlockState, IBlockReader, BlockPos)},
     * then raytracing will also be performed on this shape, to maybe correct the {@link RayTraceResult#sideHit}
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getRaytraceShape(@Nonnull IBlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return super.getRaytraceShape(state, world, pos);
    }

    /**
     * -Shape used for checking whether the block is opaque when {@link IBlockState#isSolid()} is false
     * -Shape used for checking if a side of this block is solid (for rendering)
     */
    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getRenderShape(@Nonnull IBlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
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
    public VoxelShape getShape(@Nonnull IBlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        return super.getShape(state, world, pos);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return BlockMethods.onBlockActivated(state, world, pos, player, hand, facing, hitX, hitY, hitZ, this);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest, IFluidState fluid) {
        return BlockMethods.removedByPlayer(state, world, pos, player, willHarvest, fluid, this);
    }

    @Nullable
    @Override //Old collisionRayTrace
    public RayTraceResult getRayTraceResult(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end, RayTraceResult original) {
        return BlockMethods.collisionRayTrace(state, world, pos, start, end, this);
    }

    /*
    //@Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        BlockMethods.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, isActualState, this);
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    //@Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return BlockMethods.getSelectedBoundingBox(state, world, pos, this);
    }
    */

}
