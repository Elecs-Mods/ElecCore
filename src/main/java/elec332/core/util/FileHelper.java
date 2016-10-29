package elec332.core.util;

import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

/**
 * Created by Elec332 on 18-12-2014.
 */
public class FileHelper {

    public static File getElecsModsConfigFolder(FMLPreInitializationEvent event){
        return new File(event.getModConfigurationDirectory(), "/Elec's Mods");
    }

    @SuppressWarnings("deprecation")
    public static File getConfigFileElec(FMLPreInitializationEvent event){
        return new File(getElecsModsConfigFolder(event), event.getModMetadata().modId + ".cfg");
    }

    public static File getCustomConfigFolderElec(FMLPreInitializationEvent event, String folder){
        return new File(getElecsModsConfigFolder(event), folder);
    }

    @Deprecated
    public static File getCustomConfigFileElec(FMLPreInitializationEvent event, String folder, String fileName){
        return new File(getCustomConfigFolderElec(event, folder), fileName+".cfg");
    }

}
