package elec332.core.modBaseUtils;

import net.minecraftforge.common.config.Configuration;

public class ModBase{



    public void loadConfiguration(Configuration config) {
        config.load();
        //if (config.hasChanged()){
            config.save();
        //}
    }

}
