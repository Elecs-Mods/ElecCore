package elec332.core.util.items.tools;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.helper.modInfoHelper;
import elec332.core.helper.registerHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class basePickaxe extends ItemPickaxe{
    public basePickaxe(ToolMaterial mat, String name, CreativeTabs creativetab, FMLPreInitializationEvent event){
        super(mat);
        String modName = modInfoHelper.getModID(event);
        setCreativeTab(creativetab);
        setUnlocalizedName(modName + "." + name);
        setTextureName(modName + ":" + name);
        registerHelper.registerItem(this, name);
    }
}
