package elec332.core.grid;

import elec332.core.world.DimensionCoordinate;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-7-2016.
 */
public interface ITileEntityLink extends ICapabilityProvider {

    @Nullable
    public TileEntity getTileEntity();

    @Nonnull
    public DimensionCoordinate getPosition();

    default public boolean hasChanged(){
        return false;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing);

    @Override
    @Nullable
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing);

    @Nullable
    default public Class getInformationType(){
        return null;
    }

    @Nullable
    default public Object getInformation(){
        return null;
    }

}
