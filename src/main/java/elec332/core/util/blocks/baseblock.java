package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import elec332.core.helper.registerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;

import java.util.Random;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class baseblock extends Block{
    public baseblock(Material baseMaterial, String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event, int setQuantitydropped) {
        super(baseMaterial);
        this.modID = event.getModMetadata().modId;
        this.Dropped = setQuantitydropped;
        setBlockName(modID + "." + blockName);
        setCreativeTab(CreativeTab);
        this.name = blockName;
        registerHelper.registerBlock(this, blockName);
    }

    String name;
    String modID;
    int Dropped;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        blockIcon = iconRegister.registerIcon(modID + ":" + name);
    }

    public int quantityDropped(Random random)
    {
        return Dropped;
    }
}
