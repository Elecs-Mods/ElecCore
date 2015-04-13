package elec332.core.module.event;

import elec332.core.config.ConfigWrapper;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class SetupModuleEvent{

    public SetupModuleEvent(Configuration c){
        this.configuration = c;
    }

    public SetupModuleEvent forModule(String moduleName){
        this.moduleName = moduleName;
        return this;
    }

    private Configuration configuration;
    private String moduleName;

    public Configuration getConfiguration(){
        return ConfigWrapper.wrapCategoryAsConfig(configuration, moduleName);
    }

    public Logger getModuleLog(){
        return LogManager.getLogger(this.moduleName);
    }

    public String getModuleName(){
        return this.moduleName;
    }
}
