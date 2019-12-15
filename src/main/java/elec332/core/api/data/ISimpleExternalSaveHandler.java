package elec332.core.api.data;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import java.io.File;

/**
 * Created by Elec332 on 5-7-2016.
 */
public interface ISimpleExternalSaveHandler extends IExternalSaveHandler {

    @Override
    default public void load(ISaveHandler handler, WorldInfo info, CompoundNBT tag) {
        load(handler.getWorldDirectory());
    }

    @Override
    default public CompoundNBT save(ISaveHandler handler, WorldInfo info) {
        save(handler.getWorldDirectory());
        return null;
    }

    /**
     * Invoked when the save-data should be loaded from the disk.
     *
     * @param worldDirectory The world directory
     */
    public void load(File worldDirectory);

    /**
     * Invoked when the save-data should be written to the disk.
     *
     * @param worldDirectory The world directory
     */
    public void save(File worldDirectory);

}
