package elec332.core.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;

public class ConfigCore extends Configuration{

	public ConfigCore(File file){
		super(file);
	}

	//public abstract File configFile();

	//Configuration configuration;

	//public Property isEnabled(String name, boolean def) {
	//	return configuration.get("enable", name, def).getBoolean(def);
	//}

	public Boolean isEnabled(String name, boolean defaultValue)
	{
		Property prop = get("enable", name, defaultValue);
		return prop.getBoolean();
	}

	//public boolean AddCFG(String name, String category, boolean DEFAULT_VALUE, String description){
	//	return getBoolean(name, category, DEFAULT_VALUE, description);
	//}
	public void loadConfiguration() {
		load();
		//if (config.hasChanged()){
		save();
		//}
	}

}