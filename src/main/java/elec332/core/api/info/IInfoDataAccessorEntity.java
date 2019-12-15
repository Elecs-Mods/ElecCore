package elec332.core.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorEntity extends IInfoDataAccessor {

    @Nonnull
    public PlayerEntity getPlayer();

    @Nonnull
    public Entity getEntity();

    @Nonnull
    public World getWorld();

    @Nonnull
    public CompoundNBT getData();

    public Vec3d getHitVec();

}
