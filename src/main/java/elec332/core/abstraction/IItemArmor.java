package elec332.core.abstraction;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 21-12-2016.
 */
public interface IItemArmor extends IItem {

    @Nonnull
    public ItemArmor.ArmorMaterial getArmorMaterial();

    public int getRenderIndex();

    @Nonnull
    public EntityEquipmentSlot getEquipmentSlot();

    default public int getColor(ItemStack stack){
        return getFallback().getColor(stack);
    }

    default public void removeColor(ItemStack stack){
        getFallback().removeColor(stack);
    }

    default public void setColor(ItemStack stack, int color){
        getFallback().setColor(stack, color);
    }

    /**
     * Determines if this armor will be rendered with the secondary 'overlay' texture.
     * If this is true, the first texture will be rendered using a tint of the color
     * specified by getColor(ItemStack)
     *
     * @param stack The stack
     * @return true/false
     */
    default public boolean hasOverlay(ItemStack stack){
        return getFallback().hasOverlay(stack);
    }

    @Override
    public IItemArmor getFallback();

}
