package elec332.core.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
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
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332.
 */
@SuppressWarnings("all")
@Mod(modid = ModInfo.MODID_CORE, name = ModInfo.MODNAME_CORE, dependencies = "required-after:Forge@[11.15.0.1630,)",
acceptedMinecraftVersions = ModInfo.ACCEPTEDMCVERSIONS, version = ElecCore.ElecCoreVersion, useMetadata = true, canBeDeactivated = false)
public class ElecCore extends ModBase{

	public static final String ElecCoreVersion = "9";//"#ELECCORE_VER#";

	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(ModInfo.MODID_CORE)
	public static ElecCore instance;
	public static TickHandler tickHandler;
	public static NetworkHandler networkHandler;
	public static ElecCoreCompatHandler compatHandler;
	public static Logger logger;
	private static ASMDataTable dataTable;
	private ASMDataProcessor asmDataProcessor;

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
		asmDataProcessor = new ASMDataProcessor();
		asmDataProcessor.init();
		FMLCommonHandler.instance().bus().register(tickHandler);
		debug = config.isEnabled("debug", false);
		removeJSONErrors = config.isEnabled("removeJsonExceptions", true) && !developmentEnvironment;
		ServerHelper.instance.load();

		proxy.preInitRendering();
		asmDataProcessor.process(LoaderState.PREINITIALIZATION);

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

		asmDataProcessor.process(LoaderState.INITIALIZATION);
		OredictHelper.initLists();
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		asmDataProcessor.process(LoaderState.POSTINITIALIZATION);
		OredictHelper.initLists();
		//Nope
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		asmDataProcessor.process(LoaderState.AVAILABLE);
		OredictHelper.initLists();
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

	private class ASMDataProcessor {

		private ASMDataProcessor(){
			asmLoaderMap = Maps.newHashMap();
			validStates = ImmutableList.of(LoaderState.PREINITIALIZATION, LoaderState.INITIALIZATION, LoaderState.POSTINITIALIZATION, LoaderState.AVAILABLE);
		}

		private final Map<LoaderState, List<IASMDataProcessor>> asmLoaderMap;
		private final List<LoaderState> validStates;
		private IASMDataHelper asmDataHelper;

		private void init(){
			for (LoaderState state : validStates){
				asmLoaderMap.put(state, Lists.<IASMDataProcessor>newArrayList());
			}
			asmDataHelper = new IASMDataHelper() {
				@Override
				public ASMDataTable getASMDataTable() {
					return dataTable;
				}

				@Override
				public Set<ASMDataTable.ASMData> getAnnotationList(Class<? extends Annotation> annotationClass) {
					return getASMDataTable().getAll(annotationClass.getName());
				}
			};
			for (ASMDataTable.ASMData data : asmDataHelper.getAnnotationList(elec332.core.api.annotations.ASMDataProcessor.class)){
				IASMDataProcessor dataProcessor;
				try {
					dataProcessor = (IASMDataProcessor)Class.forName(data.getClassName()).newInstance();
				} catch (Exception e){
					throw new RuntimeException("Error invocating annotated IASMData class: "+data.getClassName());
				}
				elec332.core.api.annotations.ASMDataProcessor annotation = dataProcessor.getClass().getAnnotation(elec332.core.api.annotations.ASMDataProcessor.class);
				LoaderState[] hS = annotation.value();
				if (hS == null || hS.length == 0){
					throw new IllegalArgumentException("Invalid LoaderState parameters: Null or empty array; For "+data.getClassName());
				}
				for (LoaderState state : hS){
					if (!validStates.contains(state)){
						throw new IllegalArgumentException("Invalid LoaderState parameter: "+state+"; For "+data.getClassName());
					}
					asmLoaderMap.get(state).add(dataProcessor);
				}
			}
		}

		private void process(LoaderState state){
			if (validStates.contains(state)){
				List<IASMDataProcessor> dataProcessors = asmLoaderMap.get(state);
				for (IASMDataProcessor dataProcessor : dataProcessors){
					dataProcessor.processASMData(asmDataHelper, state);
				}
				asmLoaderMap.remove(state);
			} else {
				throw new IllegalArgumentException();
			}
		}

	}

	static {
		developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

}
