package elec332.core.mcabstractionlayer.impl;

import elec332.core.mcabstractionlayer.object.IAbstractedItem;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 29-1-2017.
 */
public class MCAbstractedItem extends Item implements IAbstractedItem {

    @Override
    public final void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced) {
        addInformationC(stack, world, tooltip, advanced.isAdvanced());
    }

    @Override
    public void addInformationC(@Nonnull ItemStack stack, World world, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, world, tooltip, advanced ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
    }

    @Override
    @Nonnull
    public final ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
        return onItemRightClickC(player, hand, world);
    }

    @Override
    @Nonnull
    public ActionResult<ItemStack> onItemRightClickC(EntityPlayer player, @Nonnull EnumHand hand, World world) {
        return super.onItemRightClick(world, player, hand);
    }

    @Override
    @Nonnull
    public final EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return onItemUseFirstC(player, hand, world, pos, side, hitX, hitY, hitZ);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUseFirstC(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return super.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    @Nonnull
    public final EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return onItemUseC(player, hand, world, pos, facing, hitX, hitY, hitZ);
    }

    @Override
    @Nonnull
    public EnumActionResult onItemUseC(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public final void getSubItems(@Nonnull CreativeTabs tab, @Nonnull NonNullList<ItemStack> list) {
        getSubItemsC(this, list, tab);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItemsC(@Nonnull Item item, List<ItemStack> subItems, CreativeTabs creativeTab){
        super.getSubItems(creativeTab, (NonNullList<ItemStack>) subItems);
    }

}
