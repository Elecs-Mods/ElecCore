package elec332.core.tile;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractBlock extends Block {

    public AbstractBlock(Material blockMaterialIn, MapColor blockMapColorIn) {
        super(blockMaterialIn, blockMapColorIn);
    }

    public AbstractBlock(Material materialIn) {
        super(materialIn);
    }

    @Override
    public final boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return onBlockActivated(world, pos, player, hand, state, facing, hitX, hitY, hitZ);
    }

    protected boolean onBlockActivated(World world, BlockPos pos, EntityPlayer player, EnumHand hand, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onBlockActivated(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos p_189540_5_) {
        neighborChanged(worldIn, pos, state, blockIn, p_189540_5_);
    }

    @SuppressWarnings("deprecation")
    protected void neighborChanged(World world, BlockPos pos, IBlockState state, Block neighbor, BlockPos p_189540_5_) {
        super.neighborChanged(state, world, pos, neighbor, p_189540_5_);
    }

    @Override
    public final IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getBlockStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
    }

    @Override
    public final IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getBlockStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer, null);
    }

    public IBlockState getBlockStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @Nullable EnumHand hand) {
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    @Nullable
    @Override
    public final AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, @Nonnull IBlockAccess worldIn, @Nonnull BlockPos pos) {
        return getCollisionBoundingBox(worldIn, pos, blockState);
    }

    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, IBlockState state) {
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubBlocks(@Nonnull Item itemIn, CreativeTabs tab, NonNullList<ItemStack> list) {
        getSubBlocks(itemIn, list, tab);
    }

    @SideOnly(Side.CLIENT)
    protected void getSubBlocks(@Nonnull Item item, List<ItemStack> subBlocks, CreativeTabs creativeTab) {
        super.getSubBlocks(item, creativeTab, (NonNullList<ItemStack>) subBlocks);
    }

}
