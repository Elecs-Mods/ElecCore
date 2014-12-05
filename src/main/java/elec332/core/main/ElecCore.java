package elec332.core.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.PSF.*;
import elec332.core.config.ConfigCore;
import elec332.core.proxies.CommonProxy;
import elec332.core.util.MCModInfo;


@Mod(modid = modInfo.MODID_CORE, name = modInfo.MODNAME_CORE, dependencies = modInfo.DEPENDENCIES,
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = false)
public class ElecCore{
	
	//EXP	
	
	
	
	//END_EXP	
	
	@SidedProxy(clientSide = modInfo.CLIENTPROXY, serverSide = modInfo.COMMONPROXY)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigCore.preInit(event.getModConfigurationDirectory());
		//config.load();
		MCModInfo.CreateMCModInfo(event, modInfo.MODID_CORE, modInfo.MODNAME_CORE, "Created by Elec332",
				"Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png",
				new String[]{"Elec332"});
	}
	
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		ConfigCore.Init();
    }
}
