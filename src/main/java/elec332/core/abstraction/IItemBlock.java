package elec332.core.abstraction;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 24-12-2016.
 */
public interface IItemBlock extends IItem {

    public Block getBlock();

    @SideOnly(Side.CLIENT)
    default public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side, EntityPlayer player, ItemStack stack){
        return getFallback().canPlaceBlockOnSide(world, pos, side, player, stack);
    }

    default public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState, IDefaultBlockPlaceBehaviour defaultBlockPlaceBehaviour){
        return defaultBlockPlaceBehaviour.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState);
    }

    @Override
    public IItemBlock getFallback();

    public interface IDefaultBlockPlaceBehaviour {

        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState);

    }

}
