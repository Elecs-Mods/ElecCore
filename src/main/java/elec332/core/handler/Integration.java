package elec332.core.handler;

import cpw.mods.fml.common.Loader;
import elec332.core.main.ElecCore;

/**
 * Created by Elec332 on 17-1-2015.
 */
public class Integration {

    public static boolean NEIIntegration = IntegrationM("NotEnoughItems");

    public static void init(){
        if (!NEIIntegration)
            ElecCore.instance.info(SkipMessage("NEI"));
    }

    static Boolean IntegrationM(String name){
        return CFG(name) && Loader(name);
    }
    static Boolean CFG(String name){
        return ElecCore.instance.config.isEnabled(name, true, "Integration");
    }
    static Boolean Loader(String name){
        return Loader.isModLoaded(name);
    }
    static String SkipMessage(String string){
        return string + " wasn't detected, skipping " + string + " integration...";
    }
}
