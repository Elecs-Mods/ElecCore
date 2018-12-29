package elec332.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block implements IAbstractBlock {

    public AbstractBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public AbstractBlock(Material materialIn) {
        super(materialIn);
    }

    private String unlocalizedName;

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        if (this.unlocalizedName == null) {
            unlocalizedName = BlockMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Override
    @SuppressWarnings("deprecation")
    public RayTraceResult collisionRayTrace(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Vec3d start, @Nonnull Vec3d end) {
        return BlockMethods.collisionRayTrace(state, world, pos, start, end, this);
    }

    @Override
    @SuppressWarnings("deprecation")
    public void addCollisionBoxToList(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull AxisAlignedBB entityBox, @Nonnull List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean isActualState) {
        BlockMethods.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entityIn, isActualState, this);
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, @Nonnull World world, @Nonnull BlockPos pos) {
        return BlockMethods.getSelectedBoundingBox(state, world, pos, this);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return BlockMethods.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ, this);
    }

    @Override
    public boolean removedByPlayer(@Nonnull IBlockState state, World world, @Nonnull BlockPos pos, @Nonnull EntityPlayer player, boolean willHarvest) {
        return BlockMethods.removedByPlayer(state, world, pos, player, willHarvest, this);
    }

}
