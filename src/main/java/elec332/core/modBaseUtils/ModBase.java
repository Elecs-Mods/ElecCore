package elec332.core.modBaseUtils;

import elec332.core.config.ConfigCore;
import elec332.core.util.LogHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.io.File;

/**
 * Created by Elec332.
 */
@Deprecated
public abstract class ModBase extends LogHelper {

    public ModBase(){
        //for (int i = 0; i < 39; i++) {
            error("MOD " + Loader.instance().activeModContainer().getModId() + " IS USING OUTDATED MODBASE CLASS!");
        //}
    }

    protected abstract File configFile();

    @Override
    public abstract String modID();

    public ConfigCore config;

    @Override
    protected ConfigCore configCore() {
        return config;
    }

    public void loadConfiguration(){
        if (config == null)
            this.config = new ConfigCore(configFile());
        config.syncConfiguration();
    }

    protected void notifyEvent(FMLStateEvent event){
        info(modID() + " has " + event.getModState());
    }
    //protected void notifyEvent(FMLInitializationEvent event){
    //    info(modID() + " has " + event.getModState());
   // }
    //protected void notifyEvent(FMLPostInitializationEvent event){
    //    info(modID() + " has " + event.getModState());
   // }

    @Deprecated
    public void loadConfiguration(Configuration config) {
        config.load();
        //if (config.hasChanged()){
        config.save();
        //}
    }

    boolean outdated = false;
    boolean uptodate = false;
    String onlineVer;



}