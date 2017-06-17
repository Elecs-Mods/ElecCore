package elec332.core.item;

import elec332.core.mcabstractionlayer.impl.MCAbstractedMultipartItem;
import elec332.core.mcabstractionlayer.object.IAbstractedItem;
import mcmultipart.api.multipart.IMultipart;
import net.minecraft.block.Block;

/**
 * Created by Elec332 on 22-9-2016.
 */
public abstract class AbstractMultipartItem<T extends Block & IMultipart> extends MCAbstractedMultipartItem implements IAbstractedItem {

    public AbstractMultipartItem(T t){
        super(t);
        setRegistryName(t.getRegistryName());
        setUnlocalizedNameFromName();
    }

    protected void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
