package elec332.core.baseclasses.block;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class BaseWood extends BaseBlock {

    public BaseWood(String blockName, String modID){
        super(Material.wood, blockName, modID);
    }

   public BaseWood(String blockName, FMLPreInitializationEvent event) {
        super(Material.wood, blockName, event);
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
