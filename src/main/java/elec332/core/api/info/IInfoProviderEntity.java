package elec332.core.api.info;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoProviderEntity {

    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorEntity hitData);

    @Nonnull
    public CompoundNBT getNBTData(@Nonnull CompoundNBT tag, @Nonnull World world, @Nonnull Entity entity, @Nonnull ServerPlayerEntity player);

}
