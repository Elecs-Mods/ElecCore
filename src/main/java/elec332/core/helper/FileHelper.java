package elec332.core.helper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by Arnout on 18-12-2014.
 */
public class FileHelper {

    public static File getConfigFileElec(FMLPreInitializationEvent event){
        return new File(event.getModConfigurationDirectory() + "/Elec's_Mods", event.getModMetadata().modId + ".cfg");
    }

    public static File getCustomConfigFileElec(FMLPreInitializationEvent event, String folder, String fileName){
        return new File(event.getModConfigurationDirectory() + "/Elec's_Mods/" + folder, fileName+".cfg");
    }
}
