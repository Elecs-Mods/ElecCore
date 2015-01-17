package elec332.core.main;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.handler.integration;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.logHelper;
import elec332.core.helper.modInfoHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.modInfo;
import elec332.core.proxies.CommonProxy;
import net.minecraftforge.common.config.Configuration;


@Mod(modid = modInfo.MODID_CORE, name = modInfo.MODNAME_CORE, dependencies = "required-after:Forge@[10.13.0.1230,)",
acceptedMinecraftVersions = modInfo.ACCEPTEDMCVERSIONS, useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	protected static Configuration config;
	static String ModID;
	public static logHelper logger = new logHelper(ModID);

	//EXP	

	
	//END_EXP	
	
	@SidedProxy(clientSide = modInfo.CLIENTPROXY, serverSide = modInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(modInfo.MODID_CORE)
	public static ElecCore instance;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		integration.init();
		this.ModID = modInfoHelper.getModID(event);
		this.config = new Configuration(FileHelper.getConfigFileElec(event));
		loadConfiguration(config);

		MCModInfo.CreateMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png",	new String[]{"Elec332"});
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
