package elec332.core.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.PSF.*;
import elec332.core.config.ConfigCore;
import elec332.core.proxies.CommonProxy;



@Mod(modid = modInfo.MODID_CORE, name = modInfo.MODNAME_CORE, version = modInfo.VERSION, dependencies = modInfo.DEPENDENCIES,
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS)
public class ElecCore{
	
	//EXP	
	
	
	
	//END_EXP	
	
	@SidedProxy(clientSide = modInfo.CLIENTPROXY, serverSide = modInfo.COMMONPROXY)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ConfigCore.preInit(event.getModConfigurationDirectory());
		//config.load();
	}
	
	
	@EventHandler
    public void init(FMLInitializationEvent event) {
		ConfigCore.Init();
    }
}
