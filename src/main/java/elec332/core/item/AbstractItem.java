package elec332.core.item;

import elec332.core.mcabstractionlayer.impl.MCAbstractedItem;
import elec332.core.mcabstractionlayer.object.IAbstractedItem;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 21-8-2016.
 */
public abstract class AbstractItem extends MCAbstractedItem implements IAbstractedItem {

    public AbstractItem(){
        this(null);
    }

    public AbstractItem(ResourceLocation rl){
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
        }
    }

    @Override
    public int getItemEnchantability() {
        return super.getItemEnchantability();
    }

    public void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

}
