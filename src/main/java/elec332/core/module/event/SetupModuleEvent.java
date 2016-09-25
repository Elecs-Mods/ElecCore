package elec332.core.module.event;

import net.minecraftforge.fml.common.ModContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class SetupModuleEvent {

    public SetupModuleEvent (String moduleName, @Nonnull ModContainer mod){
        this.moduleName = moduleName;
        this.mod = mod;
        this.moduleOwner = mod.getModId();
    }

    private final ModContainer mod;
    private final String moduleName, moduleOwner;

    public Logger getModuleLog(){
        return LogManager.getLogger(this.mod.getName()+":"+this.moduleName);
    }

    public ModContainer getModuleOwner(){
        return this.mod;
    }

    public String getModuleOwnerId(){
        return this.moduleOwner;
    }

    public String getModuleName(){
        return this.moduleName;
    }

}
