package elec332.core.api.world;

import net.minecraft.world.gen.feature.IFeatureConfig;

/**
 * Created by Elec332 on 10-7-2020
 */
public interface IRetroGenFeature<C extends IFeatureConfig> {

    String DEFAULT_GENERATED_KEY = "generated";

    String getName(C config);

    default String getGenKey(C config) {
        return DEFAULT_GENERATED_KEY;
    }

    default boolean shouldRetroGen(boolean hasInitialGen, C config) {
        return !hasInitialGen;
    }

}
