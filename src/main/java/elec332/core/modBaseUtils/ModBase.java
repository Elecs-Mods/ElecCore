package elec332.core.modBaseUtils;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.config.ConfigCore;
import elec332.core.helper.LogHelper;
import elec332.core.helper.ModInfoHelper;
import elec332.core.main.ElecCore;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.config.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Elec332.
 */
public abstract class ModBase extends LogHelper {

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

    boolean outdated = false;
    boolean uptodate = false;
    String onlineVer;

    public static boolean developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    protected void runUpdateCheck(FMLPreInitializationEvent event, String versionURL){
        String modID_V = ModInfoHelper.getModID(event);
        String versionInternal = ModInfoHelper.getModVersion(event);
        event.getModLog().info("Starting version check");
        ArrayList<String> updateInfo = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(versionURL).openStream()));
                this.onlineVer = reader.readLine().replace("mod_version=", "");
            reader.close();
            //this.onlineVer ="1.3.0";

        String[] unparsed= onlineVer.replace(".", " ").split(" ");
        String qr[] = versionInternal.replace(".", " ").split(" ");
        if (unparsed.length == qr.length) {
            for (int i = 0; i < unparsed.length; i++) {
                String mr = qr[i];
                String nr = unparsed[i];
                int v = Integer.parseInt(nr);
                if (!versionInternal.equalsIgnoreCase(onlineVer)) {
                    if (v < Integer.parseInt(mr))
                        this.uptodate = true;
                    if (!uptodate)
                        if (v > Integer.parseInt(mr))
                            this.outdated = true;
                }
            }
        }else {
            event.getModLog().warn("The online version length and the internal version length differs, report this to the mod author!");
            event.getModLog().warn("Assuming you are using an outdated version");
            this.outdated = true;
        }
        if (outdated) {
            updateInfo.add(versionInternal);
            updateInfo.add(onlineVer);
            event.getModLog().info("Marking as outdated");
            ElecCore.outdatedModList.add(modID_V);
            ElecCore.Updates.put(modID_V, updateInfo);
        }else
            event.getModLog().info("Marking as up-to-date");
        event.getModLog().info("Version check complete");
        }catch (Exception e){
            event.getModLog().warn("Couldn't run VersionCheck: ", e);
        }
    }
}