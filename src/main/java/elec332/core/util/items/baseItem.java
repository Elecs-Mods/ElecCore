package elec332.core.util.items;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.helper.ModInfoHelper;
import elec332.core.helper.RegisterHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class baseItem extends Item{
    public baseItem(String name, CreativeTabs creativetab, FMLPreInitializationEvent event){
        this(name, creativetab, ModInfoHelper.getModID(event));
    }

    public baseItem(String name, CreativeTabs creativetab, String modID){
        setCreativeTab(creativetab);
        setUnlocalizedName(modID + "." + name);
        setTextureName(modID + ":" + name);
        RegisterHelper.registerItem(this, name);
    }
}
