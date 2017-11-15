package elec332.core.grid;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-7-2016.
 */
public interface ITileEntityLink extends IPositionable, ICapabilityProvider {

    @Nullable
    public TileEntity getTileEntity();

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
