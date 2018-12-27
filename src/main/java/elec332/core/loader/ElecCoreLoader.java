package elec332.core.loader;

import elec332.core.ElecCore;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.*;

/**
 * Created by Elec332 on 11-8-2018.
 */
@Mod(modid = ElecCoreLoader.MODID, name = ElecCoreLoader.MODNAME, dependencies = "after:*",
        acceptedMinecraftVersions = ElecCore.MC_VERSIONS, version = ElecCore.VERSION, useMetadata = true)
public class ElecCoreLoader {

    static final String MODID = "eleccoreloader";
    static final String MODNAME = "ElecCoreLoader";

    @Mod.Instance(MODID)
    public static ElecCoreLoader instance;
    private ASMDataHandler asmDataHandler;

    @Mod.EventHandler
    public void construct(FMLConstructionEvent event) {
        asmDataHandler = ASMDataHandler.INSTANCE; //Static load
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        asmDataHandler.identify(event.getAsmData());
        asmDataHandler.process(LoaderState.PREINITIALIZATION);
        ModuleManager.INSTANCE.init();
        ElecModHandler.INSTANCE.latePreInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        asmDataHandler.process(LoaderState.INITIALIZATION);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        asmDataHandler.process(LoaderState.POSTINITIALIZATION);
    }

    @Mod.EventHandler
    public void loadComplete(FMLLoadCompleteEvent event) {
        asmDataHandler.process(LoaderState.AVAILABLE);
    }

}
