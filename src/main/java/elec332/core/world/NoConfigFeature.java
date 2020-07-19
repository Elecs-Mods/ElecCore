package elec332.core.world;

import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

/**
 * Created by Elec332 on 10-7-2020
 */
public abstract class NoConfigFeature extends Feature<NoFeatureConfig> {

    public NoConfigFeature() {
        super(NoFeatureConfig::deserialize);
    }

}
