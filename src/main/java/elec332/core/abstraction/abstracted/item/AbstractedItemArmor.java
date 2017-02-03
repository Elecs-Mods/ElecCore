package elec332.core.abstraction.abstracted.item;

import elec332.core.abstraction.IItemArmor;
import elec332.core.api.annotations.CopyMarker;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 24-12-2016.
 */
abstract class AbstractedItemArmor extends ItemArmor implements IAbstractedItem<IItemArmor> {

    public AbstractedItemArmor(ArmorMaterial materialIn, int renderIndexIn, EntityEquipmentSlot equipmentSlotIn) {
        super(materialIn, renderIndexIn, equipmentSlotIn);
    }

    @Override @CopyMarker
    public int getColor(ItemStack stack) {
        return getLinkedItem_INTERNAL_ELEC().getColor(stack);
    }

    @Override @CopyMarker
    public void removeColor(ItemStack stack) {
        getLinkedItem_INTERNAL_ELEC().removeColor(stack);
    }

    @Override @CopyMarker
    public void setColor(ItemStack stack, int color) {
        getLinkedItem_INTERNAL_ELEC().setColor(stack, color);
    }

    @Override @CopyMarker
    public boolean hasOverlay(ItemStack stack) {
        return getLinkedItem_INTERNAL_ELEC().hasOverlay(stack);
    }

}
