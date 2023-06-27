package elec332.core.api.wrench;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 5-2-2015.
 */
public interface IWrenchable {

    ItemStack itemDropped(World world, BlockPos pos);

    boolean onWrenched(World world, BlockPos pos, Direction direction);

}
