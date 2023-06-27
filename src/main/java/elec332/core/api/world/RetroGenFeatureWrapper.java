package elec332.core.api.world;

import com.mojang.serialization.Codec;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by Elec332 on 10-7-2020
 */
public class RetroGenFeatureWrapper<C extends IFeatureConfig> extends Feature<C> implements IRetroGenFeature<C> {

    public RetroGenFeatureWrapper(Feature<C> parent, Codec<C> codec, ResourceLocation name) {
        this(parent, codec, c -> name.toString());
        setRegistryName(name);
    }

    public RetroGenFeatureWrapper(Feature<C> parent, Codec<C> codec, Function<C, String> namer) {
        super(codec);
        this.parent = parent;
        this.namer = namer;
    }

    private final Feature<C> parent;
    private final Function<C, String> namer;

    @Override
    public boolean func_241855_a(@Nonnull ISeedReader worldIn, @Nonnull ChunkGenerator generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull C config) {
        return parent.func_241855_a(worldIn, generator, rand, pos, config);
    }

    @Override
    public String getName(C config) {
        return namer.apply(config);
    }

}
