package elec332.core.tile;

import elec332.core.api.wrench.IRightClickCancel;
import elec332.core.api.wrench.IWrenchable;
import elec332.core.util.BlockStateHelper;
import elec332.core.util.DirectionHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 30-4-2015.
 */
public class BlockTileBase extends Block implements IWrenchable, ITileEntityProvider{

    public BlockTileBase(Material mat, Class<? extends TileEntity> tileClass, String blockName, String modID) {
        super(mat);
        setResistance(4.5F);
        setHardness(2.0F);
        setSoundType(SoundType.STONE); //soundTypeStone
        this.setUnlocalizedName(modID + "." + blockName);
        this.tileClass = tileClass;
        this.blockName = blockName;
        this.modID = modID;
        randomDisplayTick = IRandomDisplayTickProviderTile.class.isAssignableFrom(tileClass);
    }

    private Class<? extends TileEntity> tileClass;
    private boolean randomDisplayTick;
    public final String blockName;
    @SuppressWarnings("unused")
    public final String modID;

    @Deprecated
    public BlockTileBase registerTile(){
        GameRegistry.registerTileEntity(tileClass, blockName);
        return this;
    }

    public BlockTileBase register(){
        GameRegistry.registerBlock(this, blockName);
        return this;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return getBlockState().getBaseState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), DirectionHelper.getFacingOnPlacement(placer));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (stack != null && stack.getItem() instanceof IRightClickCancel) {
            return false;
        }
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase) {
            return ((TileBase) tile).onBlockActivated(state, player, hand, stack, side, hitX, hitY, hitZ);
        }
        return super.onBlockActivated(world, pos, state, player, hand, stack, side, hitX, hitY, hitZ);
    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer playerIn) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockClicked(playerIn);
        super.onBlockClicked(world, pos, playerIn);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        try {
            return this.tileClass.newInstance();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public ItemStack ItemDropped(World world, BlockPos pos) {
        return new ItemStack(this);
    }

    @Override
    public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
        return false;
    }

    @Override
    public void onWrenched(World world, BlockPos pos, EnumFacing forgeDirection) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onWrenched(forgeDirection);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockPlacedBy(entityLiving, stack);
        super.onBlockPlacedBy(world, pos, state, entityLiving, stack);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighbor) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onNeighborBlockChange(neighbor);
        super.onNeighborBlockChange(world, pos, state, neighbor);
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onNeighborTileChange(neighbor);
        super.onNeighborChange(world, pos, neighbor);
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockAdded();
        super.onBlockAdded(world, pos, state);
    }


    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockRemoved();
        super.breakBlock(world, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getLightValue();
        return super.getLightValue(state, world, pos);
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getLightOpacity();
        return super.getLightOpacity(state, world, pos);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random rand) {
        if (randomDisplayTick){
            TileEntity tile = WorldHelper.getTileAt(world, pos);
            if (tile != null){
                ((IRandomDisplayTickProviderTile)tile).randomDisplayTick(state, rand);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getDrops(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IRedstoneHandler)
            return ((IRedstoneHandler) tile).isProvidingWeakPower(side);
        return super.getWeakPower(state, world, pos, side);
    }

    @Override
    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IRedstoneHandler)
            return ((IRedstoneHandler) tile).canConnectRedstone(direction);
        return super.canConnectRedstone(state, world, pos, direction);
    }

    @Override
    public boolean hasComparatorInputOverride(IBlockState state) {
        return IComparatorOverride.class.isAssignableFrom(this.tileClass);
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IComparatorOverride)
            return ((IComparatorOverride) tile).getComparatorInputOverride();
        return super.getComparatorInputOverride(state, world, pos);
    }

}
