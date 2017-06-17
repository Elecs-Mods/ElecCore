package elec332.core.mcabstractionlayer.impl;

import elec332.core.abstraction.IItem;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 30-1-2017.
 */
public class MCAbstractedDefaultIItemInstance implements IItem {

    public MCAbstractedDefaultIItemInstance(Item item){
        this.item = item;
    }

    protected final Item item;

    @Nonnull
    @Override
    public ItemStack getDefaultInstance(Item item) {
        return this.item.getDefaultInstance();
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        return item.onItemRightClick(world, player, hand);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        this.item.getSubItems(tab, (NonNullList<ItemStack>) subItems);
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        return item.onItemUseFirst(player, world, pos, side, hitX, hitY, hitZ, hand);
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return item.getRGBDurabilityForDisplay(stack);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return item.canApplyAtEnchantingTable(stack, enchantment);
    }

}
