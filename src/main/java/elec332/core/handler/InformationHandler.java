package elec332.core.handler;

import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.info.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
@StaticLoad
public enum InformationHandler implements IInfoProvider, IInfoProviderEntity {

    INSTANCE;

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
        for (IInfoProvider info : ElecCoreRegistrar.INFORMATION_PROVIDERS.getAllRegisteredObjects()) {
            info.addInformation(information, hitData);
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
        for (IInfoProvider info : ElecCoreRegistrar.INFORMATION_PROVIDERS.getAllRegisteredObjects()) {
            info.gatherInformation(tag, player, hitData);
        }
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorEntity hitData) {
        for (IInfoProviderEntity info : ElecCoreRegistrar.INFORMATION_PROVIDERS_ENTITY.getAllRegisteredObjects()) {
            info.addInformation(information, hitData);
        }
    }

    @Override
    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorEntity hitData) {
        for (IInfoProviderEntity info : ElecCoreRegistrar.INFORMATION_PROVIDERS_ENTITY.getAllRegisteredObjects()) {
            info.gatherInformation(tag, player, hitData);
        }
    }

    static {
        //Block info provider
        ElecCoreRegistrar.INFORMATION_PROVIDERS.register(new IInfoProvider() {

            @Override
            public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData) {
                Block block = hitData.getBlock();
                if (block instanceof IInfoProvider) {
                    ((IInfoProvider) block).addInformation(information, hitData);
                }
                TileEntity tile = hitData.getTileEntity();
                if (tile instanceof IInfoProvider) {
                    ((IInfoProvider) tile).addInformation(information, hitData);
                }
            }

            @Override
            public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData) {
                Block block = hitData.getBlock();
                if (block instanceof IInfoProvider) {
                    ((IInfoProvider) block).gatherInformation(tag, player, hitData);
                }
                TileEntity tile = hitData.getTileEntity();
                if (tile instanceof IInfoProvider) {
                    ((IInfoProvider) tile).gatherInformation(tag, player, hitData);
                }
            }

        });
        //Entity info provider
        ElecCoreRegistrar.INFORMATION_PROVIDERS_ENTITY.register(new IInfoProviderEntity() {

            @Override
            public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorEntity hitData) {
                Entity entity = hitData.getEntity();
                if (entity instanceof IInfoProviderEntity) {
                    ((IInfoProviderEntity) entity).addInformation(information, hitData);
                }
            }

            @Override
            public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorEntity hitData) {
                Entity entity = hitData.getEntity();
                if (entity instanceof IInfoProviderEntity) {
                    ((IInfoProviderEntity) entity).gatherInformation(tag, player, hitData);
                }
            }

        });
    }

}
