package elec332.core.baseclasses.block;

//import codechicken.nei.api.ItemInfo;

import cpw.mods.fml.common.Loader;
import elec332.core.helper.RegisterHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.item.Item;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 28-12-2014.
 */
public class BaseCrop extends BlockCrops {
    public BaseCrop(String blockName, String modID){
        super();
        //this.setMaxDamage(0);
        //this.setHasSubtypes(true);
        this.setBlockName(modID + "." + blockName + "cropBlock");
        this.setBlockTextureName(modID + ":" + blockName);
        RegisterHelper.registerBlock(this, blockName + "cropBlock");
        //if (Integration.NEIIntegration)
            //ItemInfo.hiddenItems.add(new ItemStack(this));
        //seedItem = new baseSeed(blockName, modID, this);
    }

    public BaseCrop(String blockName, Item crop, String modID){
        this(blockName, modID);
        this.crop(crop);
    }

    public BaseCrop(String blockName, Item seedItem, Item crop, String modID){
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
    public boolean canBlockStay(World world, int x, int y, int z) {
        boolean AgriCraft = Loader.isModLoaded("AgriCraft");
        return !AgriCraft && super.canBlockStay(world, x, y, z);
    }

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
