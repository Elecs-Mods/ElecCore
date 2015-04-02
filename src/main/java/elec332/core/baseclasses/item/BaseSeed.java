package elec332.core.baseclasses.item;

import elec332.core.baseclasses.block.BaseCrop;
import elec332.core.helper.RegisterHelper;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;

/**
 * Created by Elec332 on 29-12-2014.
 */
public class BaseSeed extends ItemSeeds {
    public BaseSeed(String name, String modID, Item cropItem){
        this(name, modID, cropItem, new BaseCrop(name, modID));
    }

    protected BaseSeed(String name, String modID, Item cropItem, BaseCrop block) {
        super(block, Blocks.farmland);
        block.seed(this);
        block.crop(cropItem);
        this.setUnlocalizedName(modID + "." + name + "seed");
        this.setTextureName(modID + ":" + name + ".seed");
        //this.setMaxDamage(0);
        //this.setHasSubtypes(true);
        RegisterHelper.registerItem(this, name + "seed");
    }
}