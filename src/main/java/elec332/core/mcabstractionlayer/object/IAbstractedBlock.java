package elec332.core.mcabstractionlayer.object;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
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
 * Created by Elec332 on 28-1-2017.
 */
public interface IAbstractedBlock {

    public void addInformationC(@Nonnull ItemStack stack, World world, List<String> tooltip, boolean advanced);

    public boolean onBlockActivatedC(World world, BlockPos pos, EntityPlayer player, EnumHand hand, IBlockState state, EnumFacing facing, float hitX, float hitY, float hitZ);

    public void neighborChangedC(World world, BlockPos pos, IBlockState state, Block neighbor, @Nullable BlockPos fromPos);

    @Nonnull
    public IBlockState getBlockStateForPlacementC(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @Nullable EnumHand hand);

    @Nullable
    public AxisAlignedBB getCollisionBoundingBoxC(@Nonnull IBlockAccess world, @Nonnull BlockPos pos, IBlockState state);

    @SideOnly(Side.CLIENT)
    public void getSubBlocksC(@Nonnull Item item, List<ItemStack> subBlocks, CreativeTabs creativeTab);

}
