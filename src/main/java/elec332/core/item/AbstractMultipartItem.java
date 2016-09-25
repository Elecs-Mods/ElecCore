package elec332.core.item;

import mcmultipart.item.ItemMultiPart;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 22-9-2016.
 */
public abstract class AbstractMultipartItem extends ItemMultiPart {

    public AbstractMultipartItem(ResourceLocation rl){
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
            this.name = getRegistryName().toString().split(":")[1];
        } else {
            this.name = "<unknown>";
        }
    }

    protected final String name;

    public void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

}
