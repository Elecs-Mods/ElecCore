package elec332.core.config;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by Elec332.
 */
@Deprecated
public class ConfigCore extends Configuration{

	public ConfigCore(File file){
		super(file);
	}

	public Boolean isEnabled(String name, boolean defaultValue, String category)
	{
		return get(category, name, defaultValue).getBoolean();
	}

	public Boolean isEnabled(String name, boolean defaultValue)
	{
		return isEnabled(name, defaultValue, "enable");
	}

	public boolean AddCFG(String name, String category, boolean DEFAULT_VALUE, String description){
		return getBoolean(name, category, DEFAULT_VALUE, description);
	}
	public void syncConfiguration() {
		load();
		save();
	}

}