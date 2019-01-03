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
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

/**
 * Created by Elec332 on 11-8-2018.
 */
@Mod(ElecCoreLoader.MODID)
public class ElecCoreLoader {

    public ElecCoreLoader() {
        annotationDataHandler = AnnotationDataHandler.INSTANCE; //Static load
        IEventBus eventBus = FMLHelper.getModContext().getModEventBus();
        eventBus.addListener(this::preInit);
        eventBus.addListener(this::init);
        eventBus.addListener(this::postInit);
        eventBus.addListener(this::loadComplete);
        lastStage = ModLoadingStage.CONSTRUCT;
        FMLHelper.enqueueWorkAfterEvent(() -> {
            try {
                annotationDataHandler.identify(FMLHelper.getModList());
                annotationDataHandler.process(ModLoadingStage.CONSTRUCT);
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.exit(0);
            }
        });
    }

    private static final Object STAGESYNC = new Object();
    static final String MODID = "eleccoreloader";
    private static ModLoadingStage lastStage;

    private AnnotationDataHandler annotationDataHandler;

    private void preInit(FMLPreInitializationEvent event) {
        ForgeRegistries.BIOMES.getClass(); //Loads all forge registries and loads the vanilla bootstrap,
        //this is needed to be allowed to access blocks and items
        annotationDataHandler.process(ModLoadingStage.PREINIT);
        ModuleManager.INSTANCE.init();
        ElecModHandler.INSTANCE.latePreInit();
        FMLHelper.enqueueWorkAfterEvent(() -> {
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    public void init(FMLInitializationEvent event) {
        annotationDataHandler.process(ModLoadingStage.INIT);
        FMLHelper.enqueueWorkAfterEvent(() -> {
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    private void postInit(FMLPostInitializationEvent event) {
        annotationDataHandler.process(ModLoadingStage.POSTINIT);
        RegistryHelper.getBiomeRegistry().forEach(biome -> biome.addFeature(GenerationStage.Decoration.values()[GenerationStage.Decoration.values().length - 1], new CompositeFeature<>(new Feature<NoFeatureConfig>() {
            @Override
            public boolean place(IWorld world, IChunkGenerator<? extends IChunkGenSettings> chunkGenerator, Random random, BlockPos pos, NoFeatureConfig noop) {
                return WorldGenManager.INSTANCE.legacyPopulateChunk(world, chunkGenerator, random, pos);
            }
        }, IWorldGenRegister.EMPTY_FEATURE_CONFIG, FeaturePlacers.PASSTHROUGH, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG)));
        FMLHelper.enqueueWorkAfterEvent(WorldGenManager.INSTANCE::afterInit);
        FMLHelper.enqueueWorkAfterEvent(() -> {
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        annotationDataHandler.process(ModLoadingStage.COMPLETE);
        FMLHelper.enqueueWorkAfterEvent(WorldGenManager.INSTANCE::afterModsLoaded);
        FMLHelper.enqueueWorkAfterEvent(() -> {
            ElecCoreLoader.lastStage = FMLHelper.getStageFrom(event);
        });
    }

    public static ModLoadingStage getLastStage() {
        System.out.println("Returning stage: " + lastStage);
        return ModLoadingStage.PREINIT;
    }

}
