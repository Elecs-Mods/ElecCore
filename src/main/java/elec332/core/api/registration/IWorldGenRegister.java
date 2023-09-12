package elec332.core.api.registration;

import elec332.core.api.world.IBiomeGenWrapper;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.NoPlacementConfig;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IWorldGenRegister {

    public static NoFeatureConfig EMPTY_FEATURE_CONFIG = IFeatureConfig.NO_FEATURE_CONFIG;
    public static NoPlacementConfig EMPTY_PLACEMENT_CONFIG = IPlacementConfig.NO_PLACEMENT_CONFIG;

    default public void init() {
    }

    public <FC extends IFeatureConfig, PC extends IPlacementConfig> void registerFeatures(Biome biome, IBiomeGenWrapper registry);

}
