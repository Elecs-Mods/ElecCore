package elec332.core.api.world;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.IFeatureConfig;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IFeatureGenerator<C extends IFeatureConfig> {

    public String getName();

    public boolean generateFeature(IWorld world, BlockPos pos, ChunkGenerator<? extends GenerationSettings> chunkGen, Random random, C config, boolean retroGen);

    public <T> C deserialize(Dynamic<T> whatsThis);

}
