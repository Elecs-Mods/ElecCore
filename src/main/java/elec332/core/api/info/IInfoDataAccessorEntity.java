package elec332.core.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorEntity extends IInfoDataAccessor {

    @Nonnull
    @Override
    PlayerEntity getPlayer();

    @Nonnull
    Entity getEntity();

    @Nonnull
    @Override
    World getWorld();

    @Nonnull
    @Override
    CompoundNBT getData();

    @Nullable
    @Override
    Vector3d getHitVec();

}
