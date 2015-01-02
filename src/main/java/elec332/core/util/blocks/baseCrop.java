package elec332.core.util.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.registerHelper;
import elec332.core.util.items.baseSeed;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

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
