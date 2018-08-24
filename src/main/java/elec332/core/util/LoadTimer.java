package elec332.core.util;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 7-5-2016.
 */
public class LoadTimer {

    public LoadTimer(Logger logger, String mod) {
        this.logger = logger;
        this.mod = mod;
    }

    private final String mod;
    private final Logger logger;
    private long start;
    private LoaderState.ModState lastState;

    public void startPhase(FMLStateEvent event) {
        if (lastState != null) {
            throw new IllegalStateException("Cannot start phase without ending phase " + lastState + "first.");
        }
        start = System.currentTimeMillis();
        lastState = event.getModState();
    }

    public void endPhase(FMLStateEvent event) {
        LoaderState.ModState modState = event.getModState();
        if (this.lastState != modState) {
            throw new IllegalArgumentException();
        }
        lastState = null;
        logger.info(mod + " has " + modState.toString() + " in " + (System.currentTimeMillis() - start) + " ms");
    }

}
