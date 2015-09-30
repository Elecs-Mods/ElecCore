package elec332.core.main;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import elec332.core.compat.ElecCoreCompatHandler;
import elec332.core.effects.AbilityHandler;
import elec332.core.handler.FMLEventHandler;
import elec332.core.handler.TickHandler;
import elec332.core.helper.FileHelper;
import elec332.core.helper.MCModInfo;
import elec332.core.helper.ModInfoHelper;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.*;
import elec332.core.proxies.CommonProxy;
import elec332.core.server.ServerHelper;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by Elec332.
 */
@Mod(modid = ModInfo.MODID_CORE, name = ModInfo.MODNAME_CORE, dependencies = "required-after:Forge@[10.13.0.1230,)",
acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, version = "#ELECCORE_VER#", useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	public static LinkedHashMap<String, ArrayList> Updates = new LinkedHashMap<String, ArrayList>();
	public static ArrayList<String> outdatedModList = new ArrayList<String>();
	public static boolean Debug;
	//EXP	

	
	//END_EXP	
	
	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(ModInfo.MODID_CORE)
	public static ElecCore instance;
	public static TickHandler tickHandler;
	public static NetworkHandler networkHandler;
	public static ElecCoreCompatHandler compatHandler;
	public static Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		this.cfgFile = FileHelper.getConfigFileElec(event);
		this.ModID = ModInfoHelper.getModID(event);
		loadConfiguration();
		tickHandler = new TickHandler();
		networkHandler = new NetworkHandler(modID());
		networkHandler.registerClientPacket(PacketSyncWidget.class);
		networkHandler.registerServerPacket(PacketTileDataToServer.class);
		networkHandler.registerServerPacket(PacketWidgetDataToServer.class);
		networkHandler.registerClientPacket(PacketReRenderBlock.class);
		logger = event.getModLog();
		compatHandler = new ElecCoreCompatHandler(config, logger);

		//runUpdateCheck(event, "https://raw.githubusercontent.com/Elecs-Mods/ElecCore/master/build.properties");
		FMLCommonHandler.instance().bus().register(new FMLEventHandler());
		FMLCommonHandler.instance().bus().register(tickHandler);
		Debug = config.isEnabled("Debug", false);
		ServerHelper.instance.load();

		MCModInfo.CreateMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png",	new String[]{"Elec332"});
		notifyEvent(event);
	}


	@EventHandler
    public void init(FMLInitializationEvent event) {
		loadConfiguration();
		compatHandler.init();
		AbilityHandler.instance.init();
		notifyEvent(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		//Nope
	}

	public static void systemPrintDebug(Object s){
		if (Debug)
			System.out.println(s);
	}

	File cfgFile;
	String ModID;

	@Override
	public String modID(){
		return ModID;
	}

	@Override
	protected File configFile() {
		return cfgFile;
	}
}
