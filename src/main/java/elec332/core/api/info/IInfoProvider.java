package elec332.core.api.info;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoProvider {

    public void addInformation(@Nonnull IInformation information, @Nonnull IInfoDataAccessorBlock hitData);

    public void gatherInformation(@Nonnull CompoundNBT tag, @Nonnull ServerPlayerEntity player, @Nonnull IInfoDataAccessorBlock hitData);

}
