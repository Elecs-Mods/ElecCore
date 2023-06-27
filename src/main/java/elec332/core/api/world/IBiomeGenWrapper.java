package elec332.core.api.world;

import elec332.core.api.registration.IWorldGenRegister;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IBiomeGenWrapper {

    Biome getBiome();

    default void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, Placement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    default <PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, Placement<PC> placement, PC pc) {
        addFeature(decorationStage, feature, IWorldGenRegister.EMPTY_FEATURE_CONFIG, placement, pc);
    }

    default <FC extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, fc, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<PC> placement, PC pc);

    <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<FC, ? extends Feature<FC>> configuredFeature, ConfiguredPlacement<PC> placement);

    <C extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<C, ? extends Feature<C>> feature);

    <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config);

    <C extends IFeatureConfig> void addStructure(StructureFeature<C, Structure<C>> configuredStructure);

    <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig);

    <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, ConfiguredCarver<C> carver);

    void addSpawn(EntityClassification type, EntityType<? extends LivingEntity> entityType, int weight, int minGroupCount, int maxGroupCount);

    void addSpawn(EntityClassification type, MobSpawnInfo.Spawners spawnListEntry);

}
