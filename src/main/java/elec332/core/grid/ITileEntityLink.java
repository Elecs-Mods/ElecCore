package elec332.core.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
    public TileEntity getTileEntity();

    @Nonnull
    @Override
    <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing side);

    @Nullable
    default public Class getInformationType() {
        return null;
    }

    @Nullable
    default public Object getInformation() {
        return null;
    }

}
