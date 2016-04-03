package elec332.core.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 2-4-2016.
 *
 * Should only be implemented in MultiBlocks,
 * the BlockPos is the position of the tile that calls this.
 */
public interface IMultiBlockCapabilityProvider {

    public boolean hasCapability(Capability<?> capability, EnumFacing facing, @Nonnull BlockPos pos);

    public <T> T getCapability(Capability<T> capability, EnumFacing facing, @Nonnull BlockPos pos);

}
