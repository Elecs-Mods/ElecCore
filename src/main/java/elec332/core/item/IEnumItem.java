package elec332.core.item;

import elec332.core.api.client.IColoredItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 21-8-2016.
 */
public interface IEnumItem extends IColoredItem {

    /**
     * Here you can initialize your item.
     * This only gets called for the first Enum value (ordinal 0), because all values
     * use the same item.
     *
     * @param item The item
     */
    public void initializeItem(ItemEnumBased<? extends IEnumItem> item);

    /**
     * Here you can define the {@link Item.Builder} parameters for your item.
     * Used for things like setting max uses, creative tab, ect...
     * This only gets called for the first Enum value (ordinal 0), because all values
     * use the same item.
     */
    public Item.Builder getItemData();

    @Override
    default int getColorFromItemStack(ItemStack stack, int tintIndex) {
        return -1;
    }

    default public boolean shouldShow() {
        return true;
    }

    default public ResourceLocation[] getTextures() {
        return new ResourceLocation[]{
                getTextureLocation()
        };
    }

    public ResourceLocation getTextureLocation();

    default public String getUnlocalizedName(ItemStack stack) {
        return stack.getItem().getTranslationKey() + "." + ((Enum) this).name().toLowerCase();
    }

}
