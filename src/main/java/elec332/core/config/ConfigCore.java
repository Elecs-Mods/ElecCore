package elec332.core.config;

import java.io.File;
import net.minecraftforge.common.config.Configuration;



public class ConfigCore{
		
	static Configuration config;
	static File cfgFile;
	static String MODCFGCALL = "Elec_Mods";
	
	public static boolean isEnabled(String name, boolean def) {
		return config.get("enable", name, def).getBoolean(def);
	}
			  
	public static boolean CFG(String name, boolean def){
		return isEnabled(name, def);
	}
		
	public static boolean AddCFG(String name, String category, boolean DEFAULT_VALUE, String description){
		return config.getBoolean(name, category, DEFAULT_VALUE, description);
	}
	public static void preInit(File file){
		
			cfgFile = new File(file, MODCFGCALL+".cfg");
			config = new Configuration(cfgFile);
			config.load();
	}		


	public static void Init(){
			config.save();
		}
	
}
