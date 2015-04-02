package elec332.core.helper;

import cpw.mods.fml.common.FMLLog;
import elec332.core.config.ConfigCore;
import org.apache.logging.log4j.Level;

/**
 * Created by Elec332 on 17-1-2015.
 */
public abstract class LogHelper {

    protected abstract String modID();

    protected abstract ConfigCore configCore();

    public void log (Level logLevel, Object object)
    {
        FMLLog.log(modID(), logLevel, String.valueOf(object));
    }

    public void all(Object object) {
        log(Level.ALL, object);
    }

    public void debug(Object object) {
        if (configCore().get(ConfigCore.CATEGORY_GENERAL, "Log Debug", false).getBoolean())
            info(object);
    }

    public void error(Object object) {
        log(Level.ERROR, object);
    }

    public void fatal(Object object) {
        log(Level.FATAL, object);
    }

    public void info(Object object) {
        log(Level.INFO, object);
    }

    public void off(Object object) {
        log(Level.OFF, object);
    }

    public void trace(Object object) {
        log(Level.TRACE, object);
    }

    public void warn(Object object) {
        log(Level.WARN, object);
    }

}
