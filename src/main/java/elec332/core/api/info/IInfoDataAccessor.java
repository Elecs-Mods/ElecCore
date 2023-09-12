package elec332.core.api.info;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Vec3d;
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
    public PlayerEntity getPlayer();

    /**
     * The world in which the object is located
     *
     * @return The world
     */
    @Nonnull
    public World getWorld();

    @Nonnull
    public CompoundTag getData();

    /**
     * The exact position at which the object was hit
     *
     * @return The hit vec
     */
    public Vec3d getHitVec();

}
