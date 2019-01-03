package elec332.core.block;

import elec332.core.MC113ToDoReference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block implements IAbstractBlock {

    public AbstractBlock(Builder builder) {
        super(builder);
    }

    private String unlocalizedName;

    @Override
    public String getTranslationKey() {
        if (this.unlocalizedName == null) {
            unlocalizedName = BlockMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Override
    public VoxelShape getCollisionShape(IBlockState p_196268_1_, IBlockReader p_196268_2_, BlockPos p_196268_3_) {
        return super.getCollisionShape(p_196268_1_, p_196268_2_, p_196268_3_);
    }

    @Override
    public VoxelShape getRaytraceShape(IBlockState p_199600_1_, IBlockReader p_199600_2_, BlockPos p_199600_3_) {
        return super.getRaytraceShape(p_199600_1_, p_199600_2_, p_199600_3_);
    }

    @Override
    public VoxelShape getRenderShape(IBlockState p_196247_1_, IBlockReader p_196247_2_, BlockPos p_196247_3_) {
        return super.getRenderShape(p_196247_1_, p_196247_2_, p_196247_3_);
    }

    @Override
    public VoxelShape getShape(IBlockState p_196244_1_, IBlockReader p_196244_2_, BlockPos p_196244_3_) {
        return super.getShape(p_196244_1_, p_196244_2_, p_196244_3_);
    }


    //@Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTraceNonStatic(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        MC113ToDoReference.update(); //This needs to be non-static!!
        return BlockMethods.collisionRayTrace(state, world, pos, start, end, this);
    }

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

    @Override
    @SuppressWarnings("deprecation")
    public boolean onBlockActivated(IBlockState state, World world, BlockPos pos, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return BlockMethods.onBlockActivated(state, world, pos, player, hand, facing, hitX, hitY, hitZ, this);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest, IFluidState fluid) {
        return BlockMethods.removedByPlayer(state, world, pos, player, willHarvest, fluid, this);
    }

}
