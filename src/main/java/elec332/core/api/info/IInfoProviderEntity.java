package elec332.core.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoProviderEntity {

    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorEntity hitData);

    @Nonnull
    public NBTTagCompound getNBTData(@Nonnull NBTTagCompound tag, @Nonnull World world, @Nonnull Entity entity, @Nonnull EntityPlayerMP player);

}
