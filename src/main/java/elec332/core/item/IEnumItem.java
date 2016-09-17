package elec332.core.item;

import elec332.core.client.model.IColoredItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 21-8-2016.
 */
public interface IEnumItem extends IColoredItem {

    /**
     * Here you can initialize your item, like setting max uses, creative tab, ect...
     * This only gets called for the first Enum value (ordinal 0), because all values
     * use the same item.
     *
     * @param item The item
     */
    public void initializeItem(ItemEnumBased<? extends IEnumItem> item);

    @Override
    default int getColorFromItemStack(ItemStack stack, int tintIndex) {
        return -1;
    }

    default public boolean shouldShow(){
        return true;
    }

    default public ResourceLocation[] getTextures(){
        return new ResourceLocation[]{
            getTextureLocation()
        };
    }

    public ResourceLocation getTextureLocation();

}
