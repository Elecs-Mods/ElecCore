package elec332.core.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorEntity extends IInfoDataAccessor {

    @Nonnull
    @Override
    public PlayerEntity getPlayer();

    @Nonnull
    public Entity getEntity();

    @Nonnull
    @Override
    public World getWorld();

    @Nonnull
    @Override
    public CompoundNBT getData();

    @Nullable
    @Override
    public Vec3d getHitVec();

}
