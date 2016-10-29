package elec332.core.api.data;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 16-7-2016.
 */
public interface IExternalSaveHandler {

    public String getName();

    /**
     * Invoked when the save-data should be loaded from the disk.
     */
    public void load(ISaveHandler saveHandler, WorldInfo info, NBTTagCompound tag);

    /**
     * Invoked when the save-data should be written to the disk.
     *
     * @return The version at which the data was saved
     */
    @Nullable
    public NBTTagCompound save(ISaveHandler saveHandler, WorldInfo info);

    default public void nullifyData(){
    }

    default public boolean makeBackups(){
        return true;
    }

}
