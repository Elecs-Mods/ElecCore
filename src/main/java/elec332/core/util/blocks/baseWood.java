package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class baseWood extends baseblock{

   public baseWood(String blockName, CreativeTabs CreativeTab, FMLPreInitializationEvent event) {
        super(Material.wood, blockName, CreativeTab, event, 2);
    }

    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return 20;
    }

    @Override
    public boolean isFlammable(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return true;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        if (face == ForgeDirection.DOWN)
            return 20;
        else if (face != ForgeDirection.UP)
            return 10;
        else
            return 5;
    }

    @Override
    public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isWood(IBlockAccess world, int x, int y, int z) {
        return true;
    }
}
