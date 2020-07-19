package elec332.core.api.world;

import elec332.core.api.registration.IWorldGenRegister;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IBiomeGenWrapper {

    public Biome getBiome();

    default public void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, Placement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    default public <PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<NoFeatureConfig> feature, Placement<PC> placement, PC pc) {
        addFeature(decorationStage, feature, IWorldGenRegister.EMPTY_FEATURE_CONFIG, placement, pc);
    }

    default public <FC extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<NoPlacementConfig> placement) {
        addFeature(decorationStage, feature, fc, placement, IWorldGenRegister.EMPTY_PLACEMENT_CONFIG);
    }

    public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<PC> placement, PC pc);

    public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<FC, ? extends Feature<FC>> configuredFeature, ConfiguredPlacement<PC> placement);

    public <C extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<C, ? extends Feature<C>> feature);

    public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config);

    public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig);

    public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, ConfiguredCarver<C> carver);

    public void addSpawn(EntityClassification type, EntityType<? extends LivingEntity> entityType, int weight, int minGroupCount, int maxGroupCount);

    public void addSpawn(EntityClassification type, Biome.SpawnListEntry spawnListEntry);

}
