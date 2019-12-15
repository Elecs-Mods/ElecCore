package elec332.core.api.wrench;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 7-2-2015.
 */
public interface IRotatable {

    public Direction getFacing();

    public boolean rotateBlock(World world, BlockPos pos, Direction sideHit);

}
