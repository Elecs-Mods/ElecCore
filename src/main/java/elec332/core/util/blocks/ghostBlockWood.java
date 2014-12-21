package elec332.core.util.blocks;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class ghostBlockWood extends baseWood{
    public ghostBlockWood(String blockName, CreativeTabs CTab, FMLPreInitializationEvent event){
        super(blockName, CTab, event);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        return null;
    }
}
