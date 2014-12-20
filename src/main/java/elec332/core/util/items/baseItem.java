package elec332.core.util.items;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.helper.modInfoHelper;
import elec332.core.helper.registerHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class baseItem extends Item{
    public baseItem(String name, CreativeTabs creativetab, FMLPreInitializationEvent event){
        setCreativeTab(creativetab);
        setUnlocalizedName(modInfoHelper.getModID(event) + "." + name);
        setTextureName(name);
        registerHelper.registerItem(this, name);
    }
}
