package elec332.core.compat.top;

import elec332.core.api.info.IInfoDataAccessorEntity;
import elec332.core.handler.InformationHandler;
import mcjty.theoneprobe.api.IProbeHitEntityData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoEntityProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 19-1-2020
 */
public class TOPHandlerEntity implements IProbeInfoEntityProvider {

    @Override
    public String getID() {
        return "eleccore:multi_info_entity";
    }

    @Override
    public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, Entity entity, IProbeHitEntityData hitData) {
        final CompoundNBT tag = new CompoundNBT();
        IInfoDataAccessorEntity dataAccessorEntity = new IInfoDataAccessorEntity() {

            @Nonnull
            @Override
            public PlayerEntity getPlayer() {
                return playerEntity;
            }

            @Nonnull
            @Override
            public Entity getEntity() {
                return entity;
            }

            @Nonnull
            @Override
            public World getWorld() {
                return world;
            }

            @Nonnull
            @Override
            public CompoundNBT getData() {
                return tag;
            }

            @Override
            public Vector3d getHitVec() {
                return hitData.getHitVec();
            }

        };

        InformationHandler.INSTANCE.gatherInformation(tag, (ServerPlayerEntity) playerEntity, dataAccessorEntity);
        InformationHandler.INSTANCE.addInformation(new TOPInformationType(probeMode, iProbeInfo), dataAccessorEntity);
    }

}
