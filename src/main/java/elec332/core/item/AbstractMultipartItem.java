package elec332.core.item;

import elec332.abstraction.impl.MCAbstractedMultipartItem;
import elec332.abstraction.object.IAbstractedItem;
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
