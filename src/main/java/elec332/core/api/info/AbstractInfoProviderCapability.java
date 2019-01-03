package elec332.core.api.info;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 * <p>
 * Abstract IInfoProvider for a {@link Capability}
 */
@SuppressWarnings("all")
public abstract class AbstractInfoProviderCapability<O> implements IInfoProvider {

    public AbstractInfoProviderCapability(Capability<O> capability) {
        this.capability = capability;
    }

    private final Capability<O> capability;

    @Override
    public final void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        TileEntity tile = hitData.getTileEntity();
        if (tile != null) {
            OptionalCapabilityInstance<O> cap = tile.getCapability(capability, hitData.getSide());
            if (cap != null) {
                O instance = cap.orElse(null);
                if (instance != null) {
                    addInformation(information, hitData, instance);
                }
            }
        }
    }

    @Nonnull
    @Override
    public final NBTTagCompound getInfoNBTData(@Nonnull NBTTagCompound tag, TileEntity tile, @Nonnull EntityPlayerMP player, @Nonnull IInfoDataAccessorBlock hitData) {
        if (tile != null) {
            OptionalCapabilityInstance<O> cap = tile.getCapability(capability, hitData.getSide());
            if (cap != null) {
                O instance = cap.orElse(null);
                if (instance != null) {
                    getNBTData(tag, tile, instance, player, hitData);
                }
            }
        }
        return tag;
    }

    public abstract void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData, O capability);

    @Nonnull
    public abstract NBTTagCompound getNBTData(@Nonnull NBTTagCompound tag, TileEntity tile, O capability, @Nonnull EntityPlayerMP player, @Nonnull IInfoDataAccessorBlock hitData);

}
