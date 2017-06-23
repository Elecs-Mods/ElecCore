package elec332.core.tile;

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
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

/**
 * Created by Elec332 on 30-4-2015.
 */
@SuppressWarnings("deprecation")
public class BlockTileBase extends AbstractBlock implements IWrenchable, ITileEntityProvider {

    public BlockTileBase(Material mat, Class<? extends TileEntity> tileClass, String blockName, String modID) {
        this(mat, tileClass, new ResourceLocation(modID.toLowerCase(), blockName.toLowerCase()));
    }

    public BlockTileBase(Material mat, Class<? extends TileEntity> tileClass, ResourceLocation name) {
        super(mat);
        setResistance(4.5F);
        setHardness(2.0F);
        setSoundType(SoundType.STONE); //soundTypeStone
        this.setRegistryName(name);
        this.setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
        this.tileClass = tileClass;
        this.blockName = name.getResourcePath();
        this.modID = name.getResourceDomain();
        if (tileClass != null) {
            randomDisplayTick = IRandomDisplayTickProviderTile.class.isAssignableFrom(tileClass);
            comparatorInputOverride = IComparatorOverride.class.isAssignableFrom(this.tileClass);
        }
    }

    private static final EnumFacing[] HORIZONTAL, ALL;
    private Class<? extends TileEntity> tileClass;
    private boolean randomDisplayTick, comparatorInputOverride;
    public final String blockName;
    @SuppressWarnings("unused")
    public final String modID;

    @Deprecated
    public BlockTileBase registerTile(){
        GameRegistry.registerTileEntity(tileClass, blockName);
        return this;
    }

    public BlockTileBase register(){
        GameRegistry.register(this);
        return this;
    }

    @Override
    public IBlockState getBlockStateForPlacementC(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @Nullable EnumHand hand) {
        return getBlockState().getBaseState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), DirectionHelper.getFacingOnPlacement(placer));
    }



    @Override
    public boolean onBlockActivatedC(World world, BlockPos pos, EntityPlayer player, EnumHand hand, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase) {
            return ((TileBase) tile).onBlockActivated(state, player, hand, facing, hitX, hitY, hitZ);
        }
        return super.onBlockActivatedC(world, pos, player, hand, state, facing, hitX, hitY, hitZ);

    }

    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer playerIn) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockClicked(playerIn);
        super.onBlockClicked(world, pos, playerIn);
    }

    @Override
    @SuppressWarnings("all")
    public TileEntity createNewTileEntity(@Nonnull World world, int metadata) {
        try {
            return createTile(tileClass, world, metadata);
        } catch (Exception ex) {
            return null;
        }
    }

    protected TileEntity createTile(Class<? extends TileEntity> clazz, @Nonnull World world, int metadata) throws Exception {
        return clazz.newInstance();
    }

    @Override
    public ItemStack itemDropped(World world, BlockPos pos) {
        return new ItemStack(this);
    }

    @Override
    public boolean canBeReplacedByLeaves(@Nonnull IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
        return false;
    }

    @Override
    public boolean onWrenched(World world, BlockPos pos, EnumFacing forgeDirection) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        return tile instanceof TileBase && ((TileBase) tile).onWrenched(forgeDirection);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entityLiving, ItemStack stack) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockPlacedBy(entityLiving, stack);
        super.onBlockPlacedBy(world, pos, state, entityLiving, stack);
    }

    @Override //TODO: New param == changed pos??
    public void neighborChangedC(World world, BlockPos pos, IBlockState state, Block neighbor, BlockPos p_189540_5_) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onNeighborBlockChange(neighbor);
        super.neighborChangedC(world, pos, state, neighbor, p_189540_5_);
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
    public void breakBlock(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            ((TileBase) tile).onBlockRemoved();
        super.breakBlock(world, pos, state);
    }

    @Override
    public int getLightValue(@Nonnull IBlockState state, IBlockAccess world, @Nonnull BlockPos pos) {
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
    @Nonnull
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, @Nonnull IBlockState state, int fortune) {
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
        return comparatorInputOverride;
    }

    @Override
    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IComparatorOverride)
            return ((IComparatorOverride) tile).getComparatorInputOverride();
        return super.getComparatorInputOverride(state, world, pos);
    }

    //Rotating 'n stuff

    @Override
    public boolean rotateBlock(World world, @Nonnull BlockPos pos, @Nonnull EnumFacing sideHit) {
        if ((sideHit != EnumFacing.UP && sideHit != EnumFacing.DOWN) || canFaceUpOrDown(world, pos)) {
            WorldHelper.setBlockState(world, pos, getDefaultState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), sideHit), 3);
            return true;
        }
        return false;
    }

    public boolean canFaceUpOrDown(World world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        return tile instanceof TileBase && ((TileBase) tile).canFaceUpOrDown();
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState withMirror(@Nonnull IBlockState state, Mirror mirrorIn) {
        return getDefaultState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), mirrorIn.mirror(state.getValue(BlockStateHelper.FACING_NORMAL.getProperty())));
    }

    @Override
    @Nonnull
    public EnumFacing[] getValidRotations(World world, @Nonnull BlockPos pos) {
        return canFaceUpOrDown(world, pos) ? ALL : HORIZONTAL;
    }

    @Override
    @SuppressWarnings("deprecation")
    @Nonnull
    public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
        return getDefaultState().withProperty(BlockStateHelper.FACING_NORMAL.getProperty(), rot.rotate(state.getValue(BlockStateHelper.FACING_NORMAL.getProperty())));
    }

    static {
        ALL = EnumFacing.VALUES;
        HORIZONTAL = EnumFacing.HORIZONTALS;
    }

}
