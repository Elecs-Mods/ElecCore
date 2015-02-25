package elec332.core.helper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by Elec332 on 18-12-2014.
 */
public class FileHelper {

    public static File getElecConfigFolder(FMLPreInitializationEvent event){
        return new File(event.getModConfigurationDirectory(), "/Elec's Mods");
    }

    public static File getConfigFileElec(FMLPreInitializationEvent event){
        return new File(getElecConfigFolder(event), ModInfoHelper.getModID(event) + ".cfg");
    }

    public static File getCustomConfigFolderElec(FMLPreInitializationEvent event, String folder){
        return new File(getElecConfigFolder(event), folder);
    }

    public static File getCustomConfigFileElec(FMLPreInitializationEvent event, String folder, String fileName){
        return new File(getCustomConfigFolderElec(event, folder), fileName+".cfg");
    }
}
