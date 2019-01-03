package elec332.core;

import cpw.mods.modlauncher.Launcher;
import cpw.mods.modlauncher.api.IEnvironment;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.SidedProxy;
import elec332.core.api.module.IModuleController;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.grid.internal.GridEventInputHandler;
import elec332.core.handler.TickHandler;
import elec332.core.handler.event.PlayerEventHandler;
import elec332.core.inventory.window.WindowManager;
import elec332.core.network.IElecNetworkHandler;
import elec332.core.network.packets.PacketReRenderBlock;
import elec332.core.network.packets.PacketSyncWidget;
import elec332.core.network.packets.PacketTileDataToServer;
import elec332.core.network.packets.PacketWidgetDataToServer;
import elec332.core.proxies.CommonProxy;
import elec332.core.util.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332.
 */
@Mod(ElecCore.MODID)
public class ElecCore implements IModuleController, IElecCoreMod {

    public ElecCore() {
        if (instance != null) {
            throw new RuntimeException();
        }
        instance = this;
        logger = LogManager.getLogger(MODNAME);
        IEventBus eventBus = FMLHelper.getModContext().getModEventBus();
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::loadComplete);
        eventBus.addListener(this::onServerAboutToStart);
        eventBus.addListener(this::onServerStarting);
    }

    public static final String MODID = "eleccore";
    //Jap, this works...
    public static final String MODNAME = FMLHelper.getModList().getMods().stream().filter(mi -> mi.getModId().equals(MODID)).findFirst().orElseThrow(RuntimeException::new).getDisplayName();

    @SidedProxy(clientSide = "elec332.core.proxies.ClientProxy", serverSide = "elec332.core.proxies.CommonProxy")
    public static CommonProxy proxy;

    public static ElecCore instance;
    @ModNetworkHandler
    public static IElecNetworkHandler networkHandler;
    public static TickHandler tickHandler;
    public static Logger logger;
    private Configuration config;
    private LoadTimer loadTimer;

    public static final boolean developmentEnvironment;
    public static boolean debug = false;
    public static boolean removeJSONErrors = true;
    public static boolean suppressSpongeIssues = false;

    private void preInit(FMLPreInitializationEvent event) {
        this.loadTimer = new LoadTimer(logger, MODNAME);
        this.loadTimer.startPhase(event);

        this.config = new Configuration(IOHelper.getConfigFile("eleccore.cfg"));
        //this.config.load();

        //debug = config.getBoolean("debug", Configuration.CATEGORY_GENERAL, false, "Set to true to print debug info to the log.");
        //removeJSONErrors = config.getBoolean("removeJsonExceptions", Configuration.CATEGORY_CLIENT, true, "Set to true to remove all the Json model errors from the log.") && !developmentEnvironment;
        //suppressSpongeIssues = config.getBoolean("supressSpongeIssues", Configuration.CATEGORY_GENERAL, false, "Set to true to prevent multiblock crashes when Sponge is installed. WARNING: Unsupported, this may cause unexpected behaviour, use with caution!");

        tickHandler = new TickHandler();

        MC113ToDoReference.update();
        //proxy.preInitRendering();

        loadTimer.endPhase(event);
    }

    private void init(FMLInitializationEvent event) {
        loadTimer.startPhase(event);

        if (config.hasChanged()) {
            config.save();
        }

        networkHandler.registerSimplePacket(WindowManager.INSTANCE);
        networkHandler.registerAbstractPacket(PacketSyncWidget.class);
        networkHandler.registerAbstractPacket(PacketTileDataToServer.class);
        networkHandler.registerAbstractPacket(PacketWidgetDataToServer.class);
        networkHandler.registerAbstractPacket(PacketReRenderBlock.class);

        OredictHelper.initLists();

        //NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);
        MC113ToDoReference.update(this, proxy);

        loadTimer.endPhase(event);
    }

    private void postInit(FMLPostInitializationEvent event) {
        loadTimer.startPhase(event);

        OredictHelper.initLists();
        proxy.postInitRendering();
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        loadTimer.endPhase(event);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        loadTimer.startPhase(event);
        OredictHelper.initLists();

        loadTimer.endPhase(event);
    }

    private void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        GridEventInputHandler.INSTANCE.reloadHandlers();
    }

    private void onServerStarting(FMLServerStartingEvent event) {
        CommandHelper.registerCommands(event);
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        return true;
    }

    static {
        developmentEnvironment = Launcher.INSTANCE.environment().getProperty(IEnvironment.Keys.LAUNCHTARGET.get()).orElseThrow(NullPointerException::new).contains("Dev");
        //Worked from 1.6 till 1.12.2 :(
        //developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

}
