package elec332.core.main;

import elec332.core.api.annotations.RegisterTile;
import elec332.core.compat.ElecCoreCompatHandler;
import elec332.core.effects.AbilityHandler;
import elec332.core.handler.TickHandler;
import elec332.core.modBaseUtils.ModBase;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.*;
import elec332.core.proxies.CommonProxy;
import elec332.core.server.ServerHelper;
import elec332.core.util.FileHelper;
import elec332.core.util.MCModInfo;
import elec332.core.util.ModInfoHelper;
import elec332.core.util.OredictHelper;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Set;

/**
 * Created by Elec332.
 */
@SuppressWarnings("all")
@Mod(modid = ModInfo.MODID_CORE, name = ModInfo.MODNAME_CORE, dependencies = "required-after:Forge@[11.15.0.1630,)",
acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, version = ElecCore.ElecCoreVersion, useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	public static final String ElecCoreVersion = "#ELECCORE_VER#";

	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(ModInfo.MODID_CORE)
	public static ElecCore instance;
	public static TickHandler tickHandler;
	public static NetworkHandler networkHandler;
	public static ElecCoreCompatHandler compatHandler;
	public static Logger logger;
	private static ASMDataTable dataTable;

	public static final boolean developmentEnvironment;
	public static boolean oldBlocks = true;
	public static boolean debug = false;
	public static boolean removeJSONErrors = true;


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
		dataTable = event.getAsmData();

		FMLCommonHandler.instance().bus().register(tickHandler);
		debug = config.isEnabled("debug", false);
		removeJSONErrors = config.isEnabled("removeJsonExceptions", true) && !developmentEnvironment;
		ServerHelper.instance.load();

		proxy.preInitRendering();

		MCModInfo.createMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png", new String[]{"Elec332"});
		notifyEvent(event);
	}


	@EventHandler
	@SuppressWarnings("unchecked")
    public void init(FMLInitializationEvent event) {
		loadConfiguration();
		compatHandler.init();
		AbilityHandler.instance.init();
		notifyEvent(event);
		for (ASMDataTable.ASMData data : getAnnotationList(RegisterTile.class)){
			try {
				GameRegistry.registerTileEntity((Class<? extends TileEntity>) Class.forName(data.getClassName()), (String) data.getAnnotationInfo().get("name"));
			} catch (Exception e){
				logger.error("Error registering tile: "+data.getClassName());
			}
		}
		OredictHelper.initLists();
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()){

		}
		OredictHelper.initLists();
		//Nope
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		OredictHelper.initLists();
	}

	public static Set<ASMDataTable.ASMData> getAnnotationList(Class<? extends Annotation> annotationClass){
		return dataTable.getAll(annotationClass.getName());
	}

	public static void systemPrintDebug(Object s){
		if (debug) {
			System.out.println(s);
		}
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

	static {
		developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

}
