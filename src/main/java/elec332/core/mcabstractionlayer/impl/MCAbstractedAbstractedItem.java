package elec332.core.mcabstractionlayer.impl;

import elec332.core.abstraction.abstracted.item.IAbstractedItem;
import elec332.core.api.annotations.CopyMarker;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 30-1-2017.
 */
public abstract class MCAbstractedAbstractedItem extends Item implements IAbstractedItem {

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override @CopyMarker
    public ItemStack getDefaultInstance() {
        return getLinkedItem_INTERNAL_ELEC().getDefaultInstance(this);
    }

    /**
     * Returns the packed int RGB value used to render the durability bar in the GUI.
     * Defaults to a value based on the hue scaled as the damage decreases, but can be overriden.
     *
     * @param stack Stack to get durability from
     * @return A packed RGB value for the durability colour (0x00RRGGBB)
     */
    @Override @CopyMarker
    public int getRGBDurabilityForDisplay(ItemStack stack) {
        return getLinkedItem_INTERNAL_ELEC().getRGBDurabilityForDisplay(stack);
    }


    /**
     * Checks whether an item can be enchanted with a certain enchantment. This applies specifically to enchanting an item in the enchanting table and is called when retrieving the list of possible enchantments for an item.
     * Enchantments may additionally (or exclusively) be doing their own checks in {@link net.minecraft.enchantment.Enchantment#canApplyAtEnchantingTable(ItemStack)}; check the individual implementation for reference.
     * By default this will check if the enchantment type is valid for this item type.
     * @param stack the item stack to be enchanted
     * @param enchantment the enchantment to be applied
     * @return true if the enchantment can be applied to this item
     */
    @Override @CopyMarker
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return getLinkedItem_INTERNAL_ELEC().canApplyAtEnchantingTable(stack, enchantment);
    }

}
