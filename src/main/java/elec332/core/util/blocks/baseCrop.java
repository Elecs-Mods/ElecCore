package elec332.core.util.blocks;

import codechicken.nei.api.ItemInfo;
import elec332.core.handler.integration;
import elec332.core.helper.registerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 28-12-2014.
 */
public class baseCrop extends BlockCrops {
    public baseCrop(String blockName, String modID){
        super();
        //this.setMaxDamage(0);
        //this.setHasSubtypes(true);
        this.setBlockName(modID + "." + blockName + "cropBlock");
        this.setBlockTextureName(modID + ":" + blockName);
        registerHelper.registerBlock(this, blockName + "cropBlock");
        if (integration.NEIIntergration)
            ItemInfo.hiddenItems.add(new ItemStack(this));
        //seedItem = new baseSeed(blockName, modID, this);
    }

    public baseCrop(String blockName, Item crop, String modID){
        this(blockName, modID);
        this.crop(crop);
    }

    public baseCrop(String blockName, Item seedItem, Item crop, String modID){
        this(blockName, crop, modID);
        this.seed(seedItem);
    }

    public Block seed(Item seed){
        this.seed = seed;
        return this;
    }

    public Block crop(Item crop){
        this.crop = crop;
        return this;
    }

    Item seed;
    Item crop;


    @Override
    protected Item func_149866_i()
    {
        return seed;
    }

    @Override
    protected Item func_149865_P()
    {
        return crop;
    }
}
