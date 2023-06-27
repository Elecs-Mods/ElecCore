package elec332.core.compat.waila;

import elec332.core.api.info.IInfoDataAccessorEntity;
import elec332.core.handler.InformationHandler;
import mcp.mobius.waila.api.IEntityAccessor;
import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaHandlerEntity implements IEntityComponentProvider, IServerDataProvider<Entity> {

    @Override
    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, Entity entity) {
        IInfoDataAccessorEntity dataAccessorEntity = new IInfoDataAccessorEntity() {

            @Nonnull
            @Override
            public PlayerEntity getPlayer() {
                return player;
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
                return data;
            }

            @Nullable
            @Override
            public Vector3d getHitVec() {
                return null;
            }
        };
        InformationHandler.INSTANCE.gatherInformation(data, player, dataAccessorEntity);
    }

    @Override
    public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
        IInfoDataAccessorEntity dataAccessorEntity = new IInfoDataAccessorEntity() {

            @Nonnull
            @Override
            public PlayerEntity getPlayer() {
                return accessor.getPlayer();
            }

            @Nonnull
            @Override
            public Entity getEntity() {
                return accessor.getEntity();
            }

            @Nonnull
            @Override
            public World getWorld() {
                return accessor.getWorld();
            }

            @Nonnull
            @Override
            public CompoundNBT getData() {
                return accessor.getServerData();
            }

            @Override
            public Vector3d getHitVec() {
                return accessor.getHitResult().getHitVec();
            }

        };
        InformationHandler.INSTANCE.addInformation(new WailaInformationType(tooltip), dataAccessorEntity);
    }

}
