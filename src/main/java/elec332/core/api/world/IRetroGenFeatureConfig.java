package elec332.core.api.world;

import net.minecraft.world.gen.feature.IFeatureConfig;

/**
 * Created by Elec332 on 1-1-2019
 */
public interface IRetroGenFeatureConfig extends IFeatureConfig {

    String getName();

    default String getGenKey() {
        return "generated";
    }

    default boolean shouldRetroGen(boolean hasInitialGen) {
        return !hasInitialGen;
    }

}
