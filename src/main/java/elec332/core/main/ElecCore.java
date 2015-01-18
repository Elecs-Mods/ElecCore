package elec332.core.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.handler.integration;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.modInfoHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.modInfo;
import elec332.core.proxies.CommonProxy;

import java.io.File;


@Mod(modid = modInfo.MODID_CORE, name = modInfo.MODNAME_CORE, dependencies = "required-after:Forge@[10.13.0.1230,)",
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	File cfgFile;
	String ModID;

	//EXP	

	
	//END_EXP	
	
	@SidedProxy(clientSide = modInfo.CLIENTPROXY, serverSide = modInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(modInfo.MODID_CORE)
	public static ElecCore instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.cfgFile = FileHelper.getConfigFileElec(event);
		this.ModID = modInfoHelper.getModID(event);
		loadConfiguration();
		integration.init();

		MCModInfo.CreateMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png",	new String[]{"Elec332"});
		instance.info("");
	}


	@EventHandler
    public void init(FMLInitializationEvent event) {
		loadConfiguration();
    }

	@Override
	public String modID(){
		return ModID;
	}

	@Override
	protected File configFile() {
		return cfgFile;
	}
}
