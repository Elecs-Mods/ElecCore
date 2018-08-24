package elec332.core;

import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.module.IModuleController;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.compat.ModNames;
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
import elec332.core.util.CommandHelper;
import elec332.core.util.LoadTimer;
import elec332.core.util.OredictHelper;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by Elec332.
 */
@Mod(modid = ElecCore.MODID, name = ElecCore.MODNAME, dependencies = "required-after:" + ForgeVersion.MOD_ID + "@" + ElecCore.FORGE_VERSION + ";after:" + ModNames.FORESTRY,
        acceptedMinecraftVersions = ElecCore.MC_VERSIONS, version = ElecCore.VERSION, useMetadata = true)
public class ElecCore implements IModuleController, IElecCoreMod {

    public static final String VERSION = "#ELECCORE_VER#";
    public static final String MODID = "eleccore";
    public static final String MODNAME = "ElecCore";
    public static final String MC_VERSIONS = "[1.12.2,)";
    public static final String FORGE_VERSION = "[13.19.1.2195,)";

    @SidedProxy(clientSide = "elec332.core.proxies.ClientProxy", serverSide = "elec332.core.proxies.CommonProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MODID)
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

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = LogManager.getLogger("ElecCore");
        this.loadTimer = new LoadTimer(logger, MODNAME);
        this.loadTimer.startPhase(event);

        this.config = new Configuration(event.getSuggestedConfigurationFile());
        this.config.load();

        debug = config.getBoolean("debug", Configuration.CATEGORY_GENERAL, false, "Set to true to print debug info to the log.");
        removeJSONErrors = config.getBoolean("removeJsonExceptions", Configuration.CATEGORY_CLIENT, true, "Set to true to remove all the Json model errors from the log.") && !developmentEnvironment;
        suppressSpongeIssues = config.getBoolean("supressSpongeIssues", Configuration.CATEGORY_GENERAL, false, "Set to true to prevent multiblock crashes when Sponge is installed. WARNING: Unsupported, this may cause unexpected behaviour, use with caution!");

        tickHandler = new TickHandler();

        proxy.preInitRendering();

        loadTimer.endPhase(event);
    }


    @EventHandler
    public void init(FMLInitializationEvent event) {
        loadTimer.startPhase(event);

        if (config.hasChanged()) {
            config.save();
        }

        networkHandler.registerPacket(WindowManager.INSTANCE);
        networkHandler.registerClientPacket(PacketSyncWidget.class);
        networkHandler.registerServerPacket(PacketTileDataToServer.class);
        networkHandler.registerServerPacket(PacketWidgetDataToServer.class);
        networkHandler.registerClientPacket(PacketReRenderBlock.class);

        OredictHelper.initLists();

        NetworkRegistry.INSTANCE.registerGuiHandler(this, proxy);

        loadTimer.endPhase(event);
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        loadTimer.startPhase(event);

        OredictHelper.initLists();
        proxy.postInitRendering();
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());

        loadTimer.endPhase(event);
    }

    @EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        loadTimer.startPhase(event);
        OredictHelper.initLists();

        loadTimer.endPhase(event);
    }

    @EventHandler
    public void onServerAboutToStart(FMLServerAboutToStartEvent event) {
        GridEventInputHandler.INSTANCE.reloadHandlers();
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        CommandHelper.registerCommands(event);
    }

    @Override
    public boolean isModuleEnabled(String moduleName) {
        return true;
    }

    static {
        developmentEnvironment = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
    }

}
