package elec332.core.util;

import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 7-5-2016.
 * <p>
 * A load-timer, for keeping track of mod (event) loading times
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

    /**
     * Starts the timer for the specified event, should be called at the beginning of the event
     *
     * @param event The FML loading event
     */
    public void startPhase(FMLStateEvent event) {
        if (lastState != null) {
            throw new IllegalStateException("Cannot start phase without ending phase " + lastState + "first.");
        }
        start = System.currentTimeMillis();
        lastState = event.getModState();
    }

    /**
     * Stops the timer for the specified event and prints the time it took to the specified logger,
     * should be called at the end of the event
     *
     * @param event The FML loading event
     */
    public void endPhase(FMLStateEvent event) {
        LoaderState.ModState modState = event.getModState();
        if (this.lastState != modState) {
            throw new IllegalArgumentException();
        }
        lastState = null;
        logger.info(mod + " has " + modState.toString() + " in " + (System.currentTimeMillis() - start) + " ms");
    }

}
