package elec332.core.multiblock;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-5-2016.
 */
public interface ISpecialMultiBlockCapabilityProvider {

    public <T> T getSpecialCapability(Capability<T> capability, EnumFacing facing, @Nonnull BlockPos pos);

}
