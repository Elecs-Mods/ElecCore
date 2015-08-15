package elec332.core.compat;

import cpw.mods.fml.common.event.FMLInterModComms;
import elec332.core.util.AbstractCompatHandler;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 15-8-2015.
 */
public class ElecCoreCompatHandler extends AbstractCompatHandler {

    public ElecCoreCompatHandler(Configuration config, Logger logger) {
        super(config, logger);
    }

    /**
     * Use this to load EG a static boolean checking if a certain mod is loaded
     */
    @Override
    public void loadList() {
        NEI = compatEnabled("NotEnoughItems");
        WAILA = compatEnabled("Waila");

        FMLInterModComms.sendMessage("Waila", "register", "elec332.core.compat.handlers.WailaCompatHandler.register");
    }

    public static boolean NEI;
    public static boolean WAILA;
}
