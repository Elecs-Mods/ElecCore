package elec332.core.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-7-2016.
 */
public interface ITileEntityLink extends IPositionable, ICapabilityProvider {

    @Nullable
    TileEntity getTileEntity();

    @Nonnull
    @Override
    <C> LazyOptional<C> getCapability(@Nonnull Capability<C> cap, @Nullable Direction side);

    @Nullable
    default Class<?> getInformationType() {
        return null;
    }

    @Nullable
    default Object getInformation() {
        return null;
    }

}
