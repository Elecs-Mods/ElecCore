package elec332.core.baseclasses.item.tools;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.helper.ModInfoHelper;
import elec332.core.helper.RegisterHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemSword;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class BaseSword extends ItemSword{
    public BaseSword(ToolMaterial mat, String name, CreativeTabs creativetab, FMLPreInitializationEvent event){
        super(mat);
        String modName = ModInfoHelper.getModID(event);
        setCreativeTab(creativetab);
        setUnlocalizedName(modName + "." + name);
        setTextureName(modName + ":" + name);
        RegisterHelper.registerItem(this, name);
    }
}
