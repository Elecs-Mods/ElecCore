package elec332.core.api.info;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessor {

    /**
     * The player looking at the object.
     *
     * @return The player
     */
    @Nonnull
    PlayerEntity getPlayer();

    /**
     * The world in which the object is located
     *
     * @return The world
     */
    @Nonnull
    World getWorld();

    @Nonnull
    CompoundNBT getData();

    /**
     * The exact position at which the object was hit
     *
     * @return The hit vec
     */
    Vector3d getHitVec();

}
