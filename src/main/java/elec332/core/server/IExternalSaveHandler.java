package elec332.core.server;

import java.io.File;

/**
 * Created by Elec332 on 5-7-2016.
 */
public interface IExternalSaveHandler {

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
