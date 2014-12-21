package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.java.games.input.Component;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class baseBlockTile extends baseblock{
    public baseBlockTile(String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event){
        super(Material.rock, blockName, CreativeTab, event, 1);
    }
}
