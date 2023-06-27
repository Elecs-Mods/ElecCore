package elec332.core.api.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-7-2016.
 */
public interface IExternalSaveHandler {

    String getName();

    /**
     * Invoked when the save-data should be loaded from the disk.
     */
    void load(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag);

    /**
     * Invoked when the save-data should be written to the disk.
     *
     * @return The version at which the data was saved
     */
    @Nullable
    CompoundNBT save(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo);

    default void nullifyData() {
    }

    default boolean makeBackups() {
        return true;
    }

}
