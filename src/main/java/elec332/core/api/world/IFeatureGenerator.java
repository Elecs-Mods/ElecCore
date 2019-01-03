package elec332.core.api.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenSettings;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IFeatureGenerator<C extends IFeatureConfig> {

    public String getName();

    public boolean generateFeature(IWorld world, BlockPos pos, IChunkGenerator<? extends IChunkGenSettings> chunkGen, Random random, C config, boolean retroGen);

}
