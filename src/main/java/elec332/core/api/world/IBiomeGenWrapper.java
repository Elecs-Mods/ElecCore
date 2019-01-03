package elec332.core.api.world;

import elec332.core.api.registration.IWorldGenRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.carver.WorldCarverWrapper;
import net.minecraft.world.gen.feature.CompositeFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.BasePlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IBiomeGenWrapper {

    public Biome getBiome();

    default public void addFeature(GenerationStage.Decoration decorationStage, IFeatureGenerator<NoFeatureConfig> feature, BasePlacement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    default public <PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, IFeatureGenerator<NoFeatureConfig> feature, BasePlacement<PC> placement, PC pc) {
        addFeature(decorationStage, feature, IWorldGenRegister.EMPTY_FEATURE_CONFIG, placement, pc);
    }

    default public <FC extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, IFeatureGenerator<FC> feature, FC fc, BasePlacement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, fc, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, IFeatureGenerator<FC> feature, FC fc, BasePlacement<PC> placement, PC pc);

    default public void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, BasePlacement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    default public <PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, BasePlacement<PC> placement, PC pc) {
        addFeature(decorationStage, feature, IWorldGenRegister.EMPTY_FEATURE_CONFIG, placement, pc);
    }

    default public <FC extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, BasePlacement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, fc, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, BasePlacement<PC> placement, PC pc);

    public void addFeature(GenerationStage.Decoration decorationStage, CompositeFeature<?, ?> feature);

    default public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config) {
        addStructure(structure, config, structure.toString());
    }

    public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config, String name);

    public <C extends IFeatureConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig);

    public <C extends IFeatureConfig> void addCarver(GenerationStage.Carving stage, WorldCarverWrapper<C> carver);

    public void addSpawn(EnumCreatureType type, EntityType<? extends EntityLiving> entityType, int weight, int minGroupCount, int maxGroupCount);

    public void addSpawn(EnumCreatureType type, Biome.SpawnListEntry spawnListEntry);

}
