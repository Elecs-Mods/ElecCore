package elec332.core.helper;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;

/**
 * Created by Elec332 on 20-12-2014.
 */
public class modInfoHelper {

    public static String getModID(FMLPreInitializationEvent event){
        return event.getModMetadata().modId;
    }

    public static String getModname(FMLPreInitializationEvent event){
        return event.getModMetadata().name;
    }
}
