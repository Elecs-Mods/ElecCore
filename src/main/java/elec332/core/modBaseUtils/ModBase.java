package elec332.core.modBaseUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.config.ConfigCore;
import elec332.core.helper.logHelper;
import net.minecraftforge.common.config.Configuration;

import java.io.File;

public abstract class ModBase extends logHelper {

    protected abstract File configFile();

    @Override
    public abstract String modID();

    public ConfigCore config;

    public void loadConfiguration(){
        if (config == null)
            this.config = new ConfigCore(configFile());
        config.syncConfiguration();
    }

    protected void notifyEvent(FMLPreInitializationEvent event){
        info(modID() + " has " + event.getModState());
    }
    protected void notifyEvent(FMLInitializationEvent event){
        info(modID() + " has " + event.getModState());
    }
    protected void notifyEvent(FMLPostInitializationEvent event){
        info(modID() + " has " + event.getModState());
    }

    @Deprecated
    public void loadConfiguration(Configuration config) {
        config.load();
        //if (config.hasChanged()){
        config.save();
        //}
    }



}
