package elec332.core.api.client;

import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 23-12-2019
 */
public interface ITESRItem {

    @OnlyIn(Dist.CLIENT)
    public void renderItem(ItemStack stack);

}
