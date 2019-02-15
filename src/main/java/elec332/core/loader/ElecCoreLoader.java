package elec332.core.loader;

import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.util.FMLHelper;
import elec332.core.util.RegistryHelper;
import elec332.core.world.FeaturePlacers;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.Random;

/**
 * Created by Elec332 on 11-8-2018.
 */
@Mod(ElecCoreLoader.MODID)
public class ElecCoreLoader {

    public ElecCoreLoader() {
        annotationDataHandler = AnnotationDataHandler.INSTANCE; //Static load
        IEventBus eventBus = FMLHelper.getActiveModEventBus();
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::loadComplete);
        lastStage = ModLoadingStage.VALIDATE;
        FMLHelper.runLater(() -> {
            ElecCoreLoader.lastStage = ModLoadingStage.CONSTRUCT;
            annotationDataHandler.identify(FMLHelper.getModList());
            annotationDataHandler.process(ModLoadingStage.CONSTRUCT);
            ElecModHandler.INSTANCE.afterConstruct();
        });
    }

    static final String MODID = "eleccoreloader";
    private static ModLoadingStage lastStage;

    private AnnotationDataHandler annotationDataHandler;

    private void preInit(FMLCommonSetupEvent event) {
        FMLHelper.runLater(() -> {
            annotationDataHandler.process(ModLoadingStage.COMMON_SETUP);
            ModuleManager.INSTANCE.init();
            ElecModHandler.INSTANCE.latePreInit();
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
        RegistryHelper.getBiomeRegistry().forEach(biome -> biome.addFeature(GenerationStage.Decoration.values()[GenerationStage.Decoration.values().length - 1], new CompositeFeature<>(new Feature<NoFeatureConfig>() {

            @Override
            public boolean place(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoFeatureConfig noop) {
                return WorldGenManager.INSTANCE.legacyPopulateChunk(world, chunkGenerator, random, pos);
            }

        }, IWorldGenRegister.EMPTY_FEATURE_CONFIG, FeaturePlacers.PASSTHROUGH, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG)));
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
