package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class baseRock extends baseblock {

    public baseRock(String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event){
        super(Material.rock, blockName, CreativeTab, event, 1);
    }
}
