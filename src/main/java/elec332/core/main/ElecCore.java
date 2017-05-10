package elec332.core.main;

import com.google.common.eventbus.Subscribe;
import elec332.abstraction.manager.CompatASMHandler;
import elec332.core.api.IElecCoreMod;
import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.module.IModuleController;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.api.util.IDependencyHandler;
import elec332.core.api.util.IRightClickCancel;
import elec332.core.asm.ASMLoader;
import elec332.core.compat.ModNames;
import elec332.core.effects.AbilityHandler;
import elec332.core.grid.internal.GridEventHandler;
import elec332.core.grid.internal.GridEventInputHandler;
import elec332.core.handler.ModEventHandler;
import elec332.core.handler.TickHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.network.packets.PacketReRenderBlock;
import elec332.core.network.packets.PacketSyncWidget;
import elec332.core.network.packets.PacketTileDataToServer;
import elec332.core.network.packets.PacketWidgetDataToServer;
import elec332.core.proxies.CommonProxy;
import elec332.core.server.SaveHandler;
import elec332.core.server.ServerHelper;
import elec332.core.util.*;
import net.minecraft.item.ItemStack;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by Elec332.
 */
@Mod(modid = ElecCore.MODID, name = ElecCore.MODNAME, dependencies = "after:"+ ModNames.FORESTRY,
acceptedMinecraftVersions = "[1.10,)", version = ElecCore.ElecCoreVersion, useMetadata = true)
public class ElecCore implements IModuleController, IElecCoreMod, IDependencyHandler {

	public static final String ElecCoreVersion = "#ELECCORE_VER#";
	public static final String MODID = "eleccore";
	public static final String MODNAME = "ElecCore";
	public static final String FORGE_VERSION = "13.19.1.2195";
	public static final String FORGE_VERSION_110 = "12.18.3.2239";

	@SidedProxy(clientSide = "elec332.core.proxies.ClientProxy", serverSide = "elec332.core.proxies.CommonProxy")
	public static CommonProxy proxy;

	@Mod.Instance(MODID)
	public static ElecCore instance;
	@ModNetworkHandler
	public static IElecNetworkHandler networkHandler;
	public static TickHandler tickHandler;
	public static Logger logger;
	protected ElecCoreDiscoverer asmDataProcessor;
	private Configuration config;
	private LoadTimer loadTimer;
	private ModEventHandler modEventHandler;

	public static final boolean developmentEnvironment;
	public static boolean debug = false;
	public static boolean removeJSONErrors = true;

	public static boolean suppressSpongeIssues = false;

	@EventHandler
	public void construction(FMLConstructionEvent event){
		logger = LogManager.getLogger("ElecCore");
		Launch.classLoader.registerTransformer(ASMLoader.class.getCanonicalName());
		ASMLoader.injectEarly(new CompatASMHandler());
		boolean reg = false;
		List<ModContainer> mcl = FMLUtil.getLoader().getModList();
		for (int i = mcl.size() - 1; i >= 0 ; i--) { //reverse order, we want to hook into the last one
			ModContainer mc = mcl.get(i);
			if (FMLUtil.hasFMLModContainer(mc)){
				FMLModContainer fmlMc = FMLUtil.getFMLModContainer(mc);
				ModEventHooks hook = new ModEventHooks(fmlMc);
				FMLUtil.registerToModBus(fmlMc, hook);
				if (mc.getMod() == this){
					hook.onConstuct(event);
					if (i == mcl.size() - 1){
						onConstructionLast(event);
						reg = true;
					}
				}
				if (reg){
					continue;
				}
				if (FMLUtil.isModEnabled(fmlMc)){
					FMLUtil.registerToModBus(fmlMc, this);
				}
				reg = true;
			} else {
				if (mc instanceof InjectedModContainer){
					mc = FMLUtil.getWrappedContainer((InjectedModContainer) mc);
				}
				System.out.println("Last mod doesn't have a FMLModContainer, but it's ModContainer of "+mc.getClass());
			}
		}
	}

	@Subscribe
	public void onConstructionLast(FMLEvent e){
		if (e instanceof FMLConstructionEvent){
			asmDataProcessor = new ElecCoreDiscoverer();
			asmDataProcessor.identify(((FMLConstructionEvent) e).getASMHarvestedData());
			ElecModHandler.identifyMods();
			asmDataProcessor.process(LoaderState.CONSTRUCTING);
		}
	}

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ElecModHandler.initAnnotations(event.getAsmData());
		loadTimer = new LoadTimer(logger, MODNAME);
		loadTimer.startPhase(event);
		this.config = new Configuration(event.getSuggestedConfigurationFile());
		tickHandler = new TickHandler();
		networkHandler.registerClientPacket(PacketSyncWidget.class);
		networkHandler.registerServerPacket(PacketTileDataToServer.class);
		networkHandler.registerServerPacket(PacketWidgetDataToServer.class);
		networkHandler.registerClientPacket(PacketReRenderBlock.class);

		MinecraftForge.EVENT_BUS.register(tickHandler);
		config.load();
		debug = config.getBoolean("debug", Configuration.CATEGORY_GENERAL, false, "Set to true to print debug info to the log.");
		removeJSONErrors = config.getBoolean("removeJsonExceptions", Configuration.CATEGORY_CLIENT, true, "Set to true to remove all the Json model errors from the log.") && !developmentEnvironment;
		suppressSpongeIssues = config.getBoolean("supressSpongeIssues", Configuration.CATEGORY_GENERAL, false, "Set to true to prevent multiblock crashes when Sponge is installed. WARNING: Unsupported, this may cause unexpected behaviour, use with caution!");
		ServerHelper.instance.load();

		MinecraftForge.EVENT_BUS.register(new GridEventHandler());

		proxy.preInitRendering();
		asmDataProcessor.process(LoaderState.PREINITIALIZATION);
		loadTimer.endPhase(event);

		modEventHandler.postEvent(event);

		MCModInfo.createMCModInfoElec(event, "Provides core functionality for Elec's Mods",
				"-", "assets/elec332/logo.png", new String[]{"Elec332"});
	}


	@EventHandler
	@SuppressWarnings("unchecked")
    public void init(FMLInitializationEvent event) {
		loadTimer.startPhase(event);

		if (config.hasChanged()){
			config.save();
		}
		networkHandler.registerPacket(WindowManager.INSTANCE);
		ElecCoreRegistrar.dummyLoad();
		SaveHandler.INSTANCE.dummyLoad();
		AbilityHandler.instance.init();
		ElecModHandler.init();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
		asmDataProcessor.process(LoaderState.INITIALIZATION);
		OredictHelper.initLists();
		modEventHandler.postEvent(event);
		loadTimer.endPhase(event);
    }

	@EventHandler
	public void postInit(FMLPostInitializationEvent event){
		loadTimer.startPhase(event);
		asmDataProcessor.process(LoaderState.POSTINITIALIZATION);
		OredictHelper.initLists();
		proxy.postInitRendering();
		modEventHandler.postEvent(event);
		MinecraftForge.EVENT_BUS.register(new Object(){

			@SubscribeEvent(priority = EventPriority.LOWEST)
			@SuppressWarnings("all")
			public void onItemRightClick(PlayerInteractEvent.RightClickBlock event){
				ItemStack stack;
				if (event.getHand() == EnumHand.OFF_HAND){
					stack = event.getEntityPlayer().getHeldItem(EnumHand.MAIN_HAND);
					if (stack != null && stack.getItem() instanceof IRightClickCancel && ((IRightClickCancel) stack.getItem()).cancelInteraction(stack)){
						event.setCanceled(true);
						return;
					}
				}
				stack = event.getItemStack();
				if (stack != null && stack.getItem() instanceof IRightClickCancel && ((IRightClickCancel) stack.getItem()).cancelInteraction(stack)) {
					event.setCanceled(true);
					InventoryHelper.fireOnItemUse(stack.getItem(), event.getEntityPlayer(), event.getWorld(), event.getPos(), event.getHand(), event.getFace(), (float) event.getHitVec().xCoord, (float) event.getHitVec().yCoord, (float) event.getHitVec().zCoord);
				}
			}

		});
		loadTimer.endPhase(event);
	}

	@EventHandler
	public void loadComplete(FMLLoadCompleteEvent event){
		loadTimer.startPhase(event);
		asmDataProcessor.process(LoaderState.AVAILABLE);
		OredictHelper.initLists();
		modEventHandler.postEvent(event);
		loadTimer.endPhase(event);
	}

	@EventHandler
	public void onServerAboutToStart(FMLServerAboutToStartEvent event){
		GridEventInputHandler.INSTANCE.reloadHandlers();
		modEventHandler.postEvent(event);
	}

	@EventHandler
	public void onServerStarting(FMLServerStartingEvent event){
		modEventHandler.postEvent(event);
	}

	@EventHandler
	public void onServerStarted(FMLServerStartedEvent event){
		modEventHandler.postEvent(event);
	}

	@EventHandler
	public void onServerStopping(FMLServerStoppingEvent event){
		modEventHandler.postEvent(event);
	}

	@EventHandler
	public void onServerStopped(FMLServerStoppedEvent event){
		modEventHandler.postEvent(event);
	}

	@Override
	public void registerSaveHandlers(ISingleRegister<IExternalSaveHandler> saveHandlerRegistry) {
		saveHandlerRegistry.register(ServerHelper.instance);
	}

	public static void systemPrintDebug(Object s){
		if (debug) {
			System.out.println(s);
		}
	}

	@Override
	public boolean isModuleEnabled(String moduleName) {
		return true;
	}

	@Override
	public String getRequiredForgeVersion(String mcVersion) {
		return mcVersion.contains("1.10") ? FORGE_VERSION_110 : FORGE_VERSION;
	}

	public void setModEventHandler(ModEventHandler handler){
		if (this.modEventHandler != null){
			throw new IllegalStateException();
		}
		this.modEventHandler = handler;
	}

	static {
		developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	}

}
