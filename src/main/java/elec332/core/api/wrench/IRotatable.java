package elec332.core.api.wrench;

import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 7-2-2015.
 */
public interface IRotatable {

    public EnumFacing getFacing();

    public Boolean rotateBlock(World world, int x, int y, int z);

}
