package elec332.core.handler;

import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.info.*;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
    @Nonnull
    public NBTTagCompound getInfoNBTData(@Nonnull NBTTagCompound tag, TileEntity tile, @Nonnull EntityPlayerMP player, @Nonnull IInfoDataAccessorBlock hitData) {
        for (IInfoProvider info : ElecCoreRegistrar.INFORMATION_PROVIDERS.getAllRegisteredObjects()) {
            tag = info.getInfoNBTData(tag, tile, player, hitData);
        }
        return tag;
    }

    @Override
    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorEntity hitData) {
        for (IInfoProviderEntity info : ElecCoreRegistrar.INFORMATION_PROVIDERS_ENTITY.getAllRegisteredObjects()) {
            info.addInformation(information, hitData);
        }
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(@Nonnull NBTTagCompound tag, @Nonnull World world, @Nonnull Entity entity, @Nonnull EntityPlayerMP player) {
        for (IInfoProviderEntity info : ElecCoreRegistrar.INFORMATION_PROVIDERS_ENTITY.getAllRegisteredObjects()) {
            tag = info.getNBTData(tag, world, entity, player);
        }
        return tag;
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

            @Nonnull
            @Override
            public NBTTagCompound getInfoNBTData(@Nonnull NBTTagCompound tag, TileEntity tile, @Nonnull EntityPlayerMP player, @Nonnull IInfoDataAccessorBlock hitData) {
                Block block = hitData.getBlock();
                if (block instanceof IInfoProvider) {
                    tag = ((IInfoProvider) block).getInfoNBTData(tag, tile, player, hitData);
                }
                if (tile instanceof IInfoProvider) {
                    return ((IInfoProvider) tile).getInfoNBTData(tag, tile, player, hitData);
                }
                return tag;
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

            @Nonnull
            @Override
            public NBTTagCompound getNBTData(@Nonnull NBTTagCompound tag, @Nonnull World world, @Nonnull Entity entity, @Nonnull EntityPlayerMP player) {
                return entity instanceof IInfoProviderEntity ? ((IInfoProviderEntity) entity).getNBTData(tag, world, entity, player) : tag;
            }

        });
    }

}
