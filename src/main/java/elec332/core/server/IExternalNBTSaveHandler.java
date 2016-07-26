package elec332.core.server;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 16-7-2016.
 */
public interface IExternalNBTSaveHandler {

    /**
     * Invoked when the save-data should be loaded from the disk.
     *
     * @param data The saved data
     * @param saveVersion The version the data was saved at
     */
    public void load(NBTTagCompound data, int saveVersion);

    /**
     * Invoked when the save-data should be written to the disk.
     *
     * @param mainTag The tag you can save your data to
     * @return The version at which the data was saved
     */
    public int save(NBTTagCompound mainTag);

    default public boolean makeBackups(){
        return true;
    }

}
