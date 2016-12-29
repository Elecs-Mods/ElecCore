package elec332.core.item;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 26-11-2016.
 */
@SuppressWarnings("all")
public class AbstractItemBlock extends ItemBlock {

    public AbstractItemBlock(Block block) {
        super(block);
    }

    public AbstractItemBlock(Block block, ResourceLocation rl){
        this(block);
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
        }
    }

    public void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

    @Override
    @Nonnull
    public final ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return onItemRightClick(player, hand, world);
    }

    @Nonnull
    protected ActionResult<ItemStack> onItemRightClick(EntityPlayer player, @Nonnull EnumHand hand, World world) {
        return super.onItemRightClick(player.getHeldItem(hand), world, player, hand);
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return onItemUseFirst(player, hand, world, pos, side, hitX, hitY, hitZ);
    }

    protected EnumActionResult onItemUseFirst(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return super.onItemUseFirst(player.getHeldItem(hand), player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    @Nonnull
    public final EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return onItemUse(player, hand, world, pos, facing, hitX, hitY, hitZ);
    }

    @Nonnull
    protected EnumActionResult onItemUse(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player.getHeldItem(hand), player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        getSubItems(itemIn, subItems, tab);
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(@Nonnull Item item, List<ItemStack> subItems, CreativeTabs creativeTab){
        super.getSubItems(item, creativeTab, subItems);
    }

}
