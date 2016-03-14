package elec332.core.api.wrench;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 5-2-2015.
 */
public interface IWrenchable{

    public ItemStack ItemDropped(World world, BlockPos pos);

    public void onWrenched(World world, BlockPos pos, EnumFacing direction);

}
