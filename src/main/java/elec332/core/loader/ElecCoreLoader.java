package elec332.core.loader;

import elec332.core.util.FMLHelper;
import elec332.core.util.RegistryHelper;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * Created by Elec332 on 11-8-2018.
 */
@Mod(ElecCoreLoader.MODID)
public class ElecCoreLoader {

    public ElecCoreLoader() {
        annotationDataHandler = AnnotationDataHandler.INSTANCE; //Static load
        IEventBus eventBus = FMLHelper.getActiveModEventBus();
        eventBus.addGenericListener(Item.class, this::registerObjects);
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

    private void registerObjects(RegistryEvent.Register<Item> noop){
        Feature<NoFeatureConfig> featureHook = new Feature<NoFeatureConfig>(NoFeatureConfig::deserialize) {

            @Override
            public boolean place(@Nonnull IWorld world, @Nonnull ChunkGenerator<? extends GenerationSettings> chunkGenerator, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig noop) {
                return WorldGenManager.INSTANCE.legacyPopulateChunk(world, chunkGenerator, random, pos);
            }

        };
        featureHook.setRegistryName(new ResourceLocation("eleccoreloader", "featurehook"));
        RegistryHelper.getFeatures().register(featureHook);
        RegistryHelper.getBiomeRegistry().forEach(biome -> biome.addFeature(GenerationStage.Decoration.values()[GenerationStage.Decoration.values().length - 1], new ConfiguredFeature<>(featureHook, NoFeatureConfig.NO_FEATURE_CONFIG)));
    }

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
