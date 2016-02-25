package elec332.core.util;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Elec332 on 18-12-2014.
 */
public class FileHelper {

    public static File getElecsModsConfigFolder(FMLPreInitializationEvent event){
        return new File(event.getModConfigurationDirectory(), "/Elec's Mods");
    }

    @SuppressWarnings("deprecation")
    public static File getConfigFileElec(FMLPreInitializationEvent event){
        return new File(getElecsModsConfigFolder(event), ModInfoHelper.getModID(event) + ".cfg");
    }

    public static File getCustomConfigFolderElec(FMLPreInitializationEvent event, String folder){
        return new File(getElecsModsConfigFolder(event), folder);
    }

    @Deprecated
    public static File getCustomConfigFileElec(FMLPreInitializationEvent event, String folder, String fileName){
        return new File(getCustomConfigFolderElec(event, folder), fileName+".cfg");
    }

    @Nonnull
    public static InputStream getFromResource(@Nonnull ResourceLocation resourceLocation) throws IOException {
        String location = "/assets/" + resourceLocation.getResourceDomain() + "/" + resourceLocation.getResourcePath();
        InputStream ret = FileHelper.class.getResourceAsStream(location);
        if (ret != null)
            return ret;
        throw new FileNotFoundException(location);
    }

}
