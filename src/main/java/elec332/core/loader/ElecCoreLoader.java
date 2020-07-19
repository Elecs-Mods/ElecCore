package elec332.core.loader;

import elec332.core.util.FMLHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

/**
 * Created by Elec332 on 11-8-2018.
 */
@Mod(ElecCoreLoader.MODID)
public class ElecCoreLoader {

    public ElecCoreLoader() {
        annotationDataHandler = AnnotationDataHandler.INSTANCE; //Static load
        IEventBus eventBus = FMLHelper.getActiveModEventBus();
        eventBus.addGenericListener(Item.class, this::registerObjects);
        eventBus.addListener(this::createRegistries);
        eventBus.addListener(this::loadRegistries);
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::loadComplete);
        lastStage = ModLoadingStage.VALIDATE;
        FMLHelper.runLater(() -> {
            ElecCoreLoader.lastStage = ModLoadingStage.CONSTRUCT;
            annotationDataHandler.identify(FMLHelper.getModList());
            annotationDataHandler.process(ModLoadingStage.CONSTRUCT);
            ElecModHandler.INSTANCE.gatherAndInitialize(); //Load & init
            ModuleManager.INSTANCE.gatherAndConstruct(); //Load & construct
            ElecModHandler.INSTANCE.postConstruction(); // Register configs
            APIHandler.INSTANCE.postConstruction();
        });
        FMLHelper.runLater(WorldGenManager.INSTANCE::init);
    }

    static final String MODID = "eleccoreloader";
    private static ModLoadingStage lastStage;

    private final AnnotationDataHandler annotationDataHandler;

    private void registerObjects(RegistryEvent.Register<Item> noop) {
        lastStage = ModLoadingStage.LOAD_REGISTRIES;
    }

    private void createRegistries(RegistryEvent.NewRegistry event) {
        FMLHelper.runLater(() -> ElecCoreLoader.lastStage = ModLoadingStage.CREATE_REGISTRIES);
    }

    private void loadRegistries(RegistryEvent.Register<Block> event) {
        FMLHelper.runLater(() -> ElecCoreLoader.lastStage = ModLoadingStage.LOAD_REGISTRIES);
    }

    private void preInit(FMLCommonSetupEvent event) {
        FMLHelper.runLater(() -> {
            annotationDataHandler.process(ModLoadingStage.COMMON_SETUP);
            ElecModHandler.INSTANCE.postSetup();
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    public void init(InterModEnqueueEvent event) {
        FMLHelper.runLater(() -> {
            annotationDataHandler.process(ModLoadingStage.ENQUEUE_IMC);
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    private void postInit(InterModProcessEvent event) {
        FMLHelper.runLater(() -> {
            annotationDataHandler.process(ModLoadingStage.PROCESS_IMC);
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
        FMLHelper.runLater(WorldGenManager.INSTANCE::afterInit);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        FMLHelper.runLater(() -> {
            annotationDataHandler.process(ModLoadingStage.COMPLETE);
            WorldGenManager.INSTANCE.afterModsLoaded();
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    public static ModLoadingStage getLastStage() {
        return lastStage;
    }

}
