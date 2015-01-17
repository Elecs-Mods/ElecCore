package elec332.core.handler;

import cpw.mods.fml.common.Loader;
import elec332.core.main.ElecCore;

/**
 * Created by Elec332 on 17-1-2015.
 */
public class integration {

    public static boolean NEIIntergration = Loader.isModLoaded("NotEnoughItems");

    public static void init(){
        if (!NEIIntergration)
            ElecCore.logger.info("No NEI detected, skipping NEI integration...");
    }

}
