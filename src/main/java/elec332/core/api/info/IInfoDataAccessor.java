package elec332.core.api.info;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessor {

    @Nonnull
    public EntityPlayer getPlayer();

    @Nonnull
    public World getWorld();

    @Nonnull
    public NBTTagCompound getData();

    public Vec3d getHitVec();

}
