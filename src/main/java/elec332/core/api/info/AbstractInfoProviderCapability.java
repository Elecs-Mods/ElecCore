package elec332.core.api.info;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

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
            LazyOptional<O> cap = tile.getCapability(capability, hitData.getSide());
            if (cap != null) {
                O instance = cap.orElse(null);
                if (instance != null) {
                    addInformation(information, hitData, instance);
                }
            }
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundTag tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        TileEntity tile = hitData.getTileEntity();
        if (tile != null) {
            LazyOptional<O> cap = tile.getCapability(capability, hitData.getSide());
            if (cap != null) {
                O instance = cap.orElse(null);
                if (instance != null) {
                    getNBTData(tag, instance, player, hitData);
                }
            }
        }
    }

    public abstract void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData, O capability);

    @Nonnull
    public abstract CompoundTag getNBTData(@Nonnull CompoundTag tag, O capability, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData);

}
