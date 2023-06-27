package elec332.core.api.storage;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.IServerConfiguration;
import net.minecraft.world.storage.SaveFormat;

import java.io.File;

/**
 * Created by Elec332 on 5-7-2016.
 */
public interface ISimpleExternalSaveHandler extends IExternalSaveHandler {

    @Override
    default void load(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo, CompoundNBT tag) {
        load(levelSave.getWorldDir().toFile());
    }

    @Override
    default CompoundNBT save(SaveFormat.LevelSave levelSave, IServerConfiguration serverInfo) {
        save(levelSave.getWorldDir().toFile());
        return null;
    }

    /**
     * Invoked when the save-data should be loaded from the disk.
     *
     * @param worldDirectory The world directory
     */
    void load(File worldDirectory);

    /**
     * Invoked when the save-data should be written to the disk.
     *
     * @param worldDirectory The world directory
     */
    void save(File worldDirectory);

}
