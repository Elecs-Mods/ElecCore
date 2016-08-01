package elec332.core.main;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.compat.ElecCoreCompatHandler;
import elec332.core.effects.AbilityHandler;
import elec332.core.grid.v2.internal.GridEventHandler;
import elec332.core.grid.v2.internal.GridEventInputHandler;
import elec332.core.handler.TickHandler;
import elec332.core.modBaseUtils.ModInfo;
import elec332.core.network.*;
import elec332.core.proxies.CommonProxy;
import elec332.core.server.ServerHelper;
import elec332.core.util.FileHelper;
import elec332.core.util.LoadTimer;
import elec332.core.util.MCModInfo;
import elec332.core.util.OredictHelper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332.
 */
@Mod(modid = ElecCore.MODID, name = ElecCore.MODNAME, dependencies = "required-after:Forge@[12.16.0.1832,)",
acceptedMinecraftVersions = "[1.9,)", version = ElecCore.ElecCoreVersion, useMetadata = true)
public class ElecCore {

	public static final String ElecCoreVersion = "#ELECCORE_VER#";
	public static final String MODID = "ElecCore";
	public static final String MODNAME = "ElecCore";

	@SidedProxy(clientSide = ModInfo.CLIENTPROXY, serverSide = ModInfo.COMMONPROXY)
	public static CommonProxy proxy;

	@Mod.Instance(MODID)
	public static ElecCore instance;
	public static TickHandler tickHandler;
	public static NetworkHandler networkHandler;
	public static ElecCoreCompatHandler compatHandler;
	public static Logger logger;
	private static ASMDataTable dataTable;
	private ASMDataProcessor asmDataProcessor;
	private Configuration config;
	private LoadTimer loadTimer;

	public static final boolean developmentEnvironment;
	@Deprecated
	public static boolean oldBlocks = true;
	public static boolean debug = false;
	public static boolean removeJSONErrors = true;


	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		logger = event.getModLog();
		loadTimer = new LoadTimer(logger, MODNAME);
		loadTimer.startPhase(event);
		this.config = new Configuration(FileHelper.getConfigFileElec(event));
		tickHandler = new TickHandler();
		networkHandler = new NetworkHandler(MODID);
		networkHandler.registerClientPacket(PacketSyncWidget.class);
		networkHandler.registerServerPacket(PacketTileDataToServer.class);
		networkHandler.registerServerPacket(PacketWidgetDataToServer.class);
		networkHandler.registerClientPacket(PacketReRenderBlock.class);

		compatHandler = new ElecCoreCompatHandler(config, logger);
		dataTable = event.getAsmData();
		asmDataProcessor = new ASMDataProcessor();
		asmDataProcessor.init();
		MinecraftForge.EVENT_BUS.register(tickHandler);
		debug = config.getBoolean("debug", Configuration.CATEGORY_GENERAL, false, "Set to true to print debug info to the log.");
		removeJSONErrors = config.getBoolean("removeJsonExceptions", Configuration.CATEGORY_CLIENT, true, "Set to true to remove all the Json model errors from the log.") && !developmentEnvironment;
		ServerHelper.instance.load();

		MinecraftForge.EVENT_BUS.register(new GridEventHandler());

		proxy.preInitRendering();
		asmDataProcessor.process(LoaderState.PREINITIALIZATION);

		loadTimer.endPhase(event);
		MCModInfo.createMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png", new String[]{"Elec332"});
	}


	@EventHandler
	@SuppressWarnings("unchecked")
    public void init(FMLInitializationEvent event) {
		loadTimer.startPhase(event);
		config.load();
		if (config.hasChanged()){
			config.save();
		}
		compatHandler.init();
		AbilityHandler.instance.init();

		asmDataProcessor.process(LoaderState.INITIALIZATION);
		OredictHelper.initLists();
		loadTimer.endPhase(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		loadTimer.startPhase(event);
		asmDataProcessor.process(LoaderState.POSTINITIALIZATION);
		OredictHelper.initLists();
		loadTimer.endPhase(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		loadTimer.startPhase(event);
		asmDataProcessor.process(LoaderState.AVAILABLE);
		OredictHelper.initLists();
		loadTimer.endPhase(event);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		GridEventInputHandler.INSTANCE.reloadHandlers();
	}

	public static void systemPrintDebug(Object s){
		if (debug) {
			System.out.println(s);
		}
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
				} catch(ClassNotFoundException e){
					//Do nothing, class is probably annotated with @SideOnly
					continue;
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
