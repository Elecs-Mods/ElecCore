package elec332.core.baseclasses.item.tools;

import elec332.core.helper.ModInfoHelper;
import elec332.core.helper.RegisterHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemPickaxe;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class BasePickaxe extends ItemPickaxe{
    public BasePickaxe(ToolMaterial mat, String name, CreativeTabs creativetab, FMLPreInitializationEvent event){
        super(mat);
        String modName = ModInfoHelper.getModID(event);
        setCreativeTab(creativetab);
        setUnlocalizedName(modName + "." + name);
        //setTextureName(modName + ":" + name);
        RegisterHelper.registerItem(this, name);
    }
}
