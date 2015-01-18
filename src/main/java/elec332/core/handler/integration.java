package elec332.core.handler;

import cpw.mods.fml.common.Loader;
import elec332.core.main.ElecCore;

/**
 * Created by Elec332 on 17-1-2015.
 */
public class integration {

    public static boolean NEIIntergration = CFG("NotEnoughItems") && Loader.isModLoaded("NotEnoughItems");

    public static void init(){
        if (!NEIIntergration)
            ElecCore.instance.info("No NEI detected, skipping NEI integration...");
    }

    static Boolean CFG(String name){
        return ElecCore.instance.config.isEnabled(name, true, "Integration");
    }
}
