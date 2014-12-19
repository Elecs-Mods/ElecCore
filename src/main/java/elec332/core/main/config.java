package elec332.core.main;

import elec332.core.config.ConfigCore;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Elec332 on 19-12-2014.
 */
public class config extends ConfigCore{
    static Configuration config = ElecCore.config;

    public static boolean isEnabled(String name, boolean def) {
        return isEnabled(config, name, def);
    }

    public static boolean AddCFG(String name, String category, boolean DEFAULT_VALUE, String description) {
        return AddCFG(config, name, category, DEFAULT_VALUE, description);
    }
}
