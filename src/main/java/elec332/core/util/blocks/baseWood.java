package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.registerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;

import java.util.Random;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class baseWood extends baseblock{

   public baseWood(String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event) {
        super(Material.wood, blockName, CreativeTab, event, 2);
        Blocks.fire.func_149842_a(getIdFromBlock(this), 5, 20);
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z)
    {
        return true;
    }
}
