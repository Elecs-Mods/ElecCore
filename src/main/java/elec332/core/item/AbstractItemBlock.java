package elec332.core.item;

import elec332.core.mcabstractionlayer.impl.MCAbstractedItemBlock;
import elec332.core.mcabstractionlayer.object.IAbstractedItem;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 26-11-2016.
 */
@SuppressWarnings("all")
public class AbstractItemBlock extends MCAbstractedItemBlock implements IAbstractedItem {

    public AbstractItemBlock(Block block) {
        super(block);
    }

    public AbstractItemBlock(Block block, ResourceLocation rl){
        this(block);
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
        }
    }

    public void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

}
