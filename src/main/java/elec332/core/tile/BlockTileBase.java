package elec332.core.tile;

import elec332.core.api.wrench.IRightClickCancel;
import elec332.core.api.wrench.IWrenchable;
import elec332.core.util.RegisterHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;

/**
 * Created by Elec332 on 30-4-2015.
 */
public class BlockTileBase extends Block implements IWrenchable, ITileEntityProvider{

    public BlockTileBase(Material mat, Class<? extends TileEntity> tileClass, String blockName, String modID) {
        super(mat);
        setResistance(4.5F);
        setHardness(2.0F);
        setStepSound(soundTypeStone);
        this.setUnlocalizedName(modID + "." + blockName);
        this.tileClass = tileClass;
        this.blockName = blockName;
        this.modID = modID;
    }

   // public IIcon[][] icons = new IIcon[2][6];
    private Class<? extends TileEntity> tileClass;
    private String blockName;
    private String modID;

    public BlockTileBase registerTile(){
        GameRegistry.registerTileEntity(tileClass, blockName);
        return this;
    }

    public BlockTileBase register(){
        RegisterHelper.registerBlock(this, blockName);
        return this;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.getHeldItem() != null && player.getHeldItem().getItem() instanceof IRightClickCancel)
            return false;
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).onBlockActivated(player, side, hitX, hitY, hitZ);
        return super.onBlockActivated(world, pos, state, player, side, hitX, hitY, hitZ);
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
    public boolean canBeReplacedByLeaves(IBlockAccess world, BlockPos pos) {
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
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getLightValue();
        return super.getLightValue(world, pos);
    }

    @Override
    public int getLightOpacity(IBlockAccess world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getLightOpacity();
        return super.getLightOpacity(world, pos);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof TileBase)
            return ((TileBase) tile).getDrops(fortune);
        return super.getDrops(world, pos, state, fortune);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IRedstoneHandler)
            return ((IRedstoneHandler) tile).isProvidingWeakPower(side);
        return super.isProvidingWeakPower(world, pos, state, side);
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing direction) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IRedstoneHandler)
            return ((IRedstoneHandler) tile).canConnectRedstone(direction);
        return super.canConnectRedstone(world, pos, direction);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return IComparatorOverride.class.isAssignableFrom(this.tileClass);
    }

    @Override
    public int getComparatorInputOverride(World world, BlockPos pos) {
        TileEntity tile = WorldHelper.getTileAt(world, pos);
        if (tile instanceof IComparatorOverride)
            return ((IComparatorOverride) tile).getComparatorInputOverride();
        return super.getComparatorInputOverride(world, pos);
    }
/*
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return icons[0][DirectionHelper.ROTATION_MATRIX_YAW[2][side]];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        int meta = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        int i = tile instanceof IActivatableMachine && ((IActivatableMachine)tile).isActive() ? 1 : 0;
        return icons[i][DirectionHelper.ROTATION_MATRIX_YAW[meta][side]];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister){
        icons[0][0] = iconRegister.registerIcon(getBottomIconName(true));
        icons[0][1] = iconRegister.registerIcon(getTopIconName(true));
        icons[0][2] = iconRegister.registerIcon(getFrontTexture(true));
        icons[0][3] = iconRegister.registerIcon(getSideTexture(true, BlockSide.BACK));
        icons[0][4] = iconRegister.registerIcon(getSideTexture(true, BlockSide.RIGHT));
        icons[0][5] = iconRegister.registerIcon(getSideTexture(true, BlockSide.LEFT));
        icons[1][0] = iconRegister.registerIcon(getBottomIconName(false));
        icons[1][1] = iconRegister.registerIcon(getTopIconName(false));
        icons[1][2] = iconRegister.registerIcon(getFrontTexture(false));
        icons[1][3] = iconRegister.registerIcon(getSideTexture(false, BlockSide.BACK));
        icons[1][4] = iconRegister.registerIcon(getSideTexture(false, BlockSide.RIGHT));
        icons[1][5] = iconRegister.registerIcon(getSideTexture(false, BlockSide.LEFT));
    }

    @SideOnly(Side.CLIENT)
    public String getSideTexture(boolean active, BlockSide side) {
        return getTextureName() + "_side";
    }

    @SideOnly(Side.CLIENT)
    public String getFrontTexture(boolean active){
        return getTextureName() + "_front";
    }

    @SideOnly(Side.CLIENT)
    public String getTopIconName(boolean active) {
        return getTextureName() + "_top";
    }

    @SideOnly(Side.CLIENT)
    public String getBottomIconName(boolean active) {
        return getTextureName() + "_bottom";
    }

    @Override
    @SideOnly(Side.CLIENT)
    protected String getTextureName(){
        if (this.textureName != null)
            return textureName;
        return modID + ":" + blockName;
    }*/
}
