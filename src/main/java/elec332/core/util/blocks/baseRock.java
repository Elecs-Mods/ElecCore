package elec332.core.util.blocks;


import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.registerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class baseRock extends baseblock {

    public baseRock(String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event){
        super(Material.rock, blockName, CreativeTab, event, 1);

    }
}
