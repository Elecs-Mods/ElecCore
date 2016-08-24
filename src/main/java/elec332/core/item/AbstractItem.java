package elec332.core.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 21-8-2016.
 */
public abstract class AbstractItem extends Item {

    public AbstractItem(ResourceLocation rl){
        setRegistryName(rl);
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

}
