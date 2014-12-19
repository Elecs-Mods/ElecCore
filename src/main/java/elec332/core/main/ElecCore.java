package elec332.core.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.helper.FileHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.modInfo;
import elec332.core.proxies.CommonProxy;
import elec332.core.helper.MCModInfo;
import net.minecraftforge.common.config.Configuration;

import java.lang.*;


@Mod(modid = modInfo.MODID_CORE, name = modInfo.MODNAME_CORE, dependencies = modInfo.DEPENDENCIES,
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	protected static Configuration config;

	//EXP	

	
	//END_EXP	
	
	@SidedProxy(clientSide = modInfo.CLIENTPROXY, serverSide = modInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(modInfo.MODID_CORE)
	public ElecCore instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.config = new Configuration(FileHelper.getConfigFileElec(event));
		loadConfiguration(config);

		MCModInfo.CreateMCModInfo(event, modInfo.MODID_CORE, modInfo.MODNAME_CORE, "Created by Elec332",
				"Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png",
				new String[]{"Elec332"});
		if(elec332.core.main.config.isEnabled("TestItemEXP22222222", true)) {
			System.out.println("jkhvhjtfydfyj");
		}
	}


	@EventHandler
    public void init(FMLInitializationEvent event) {
		loadConfiguration(config);
		//ConfigCore.loadConfiguration(config);
    }
}
