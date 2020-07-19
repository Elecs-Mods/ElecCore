package elec332.core.api.registration;

import elec332.core.api.world.IBiomeGenWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;
import net.minecraftforge.registries.IForgeRegistry;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IWorldGenRegister {

    NoFeatureConfig EMPTY_FEATURE_CONFIG = IFeatureConfig.NO_FEATURE_CONFIG;
    NoPlacementConfig EMPTY_PLACEMENT_CONFIG = IPlacementConfig.NO_PLACEMENT_CONFIG;

    default void init() {
    }

    default void registerFeatures(IForgeRegistry<Feature<?>> featureRegistry) {
    }

    void configureBiome(Biome biome, IBiomeGenWrapper registry);

}
