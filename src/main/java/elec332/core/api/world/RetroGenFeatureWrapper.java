package elec332.core.api.world;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Function;

/**
 * Created by Elec332 on 10-7-2020
 */
public class RetroGenFeatureWrapper<C extends IFeatureConfig> extends Feature<C> implements IRetroGenFeature<C> {

    public RetroGenFeatureWrapper(Feature<C> parent, ResourceLocation name) {
        this(parent, c -> name.toString());
        setRegistryName(name);
    }

    public RetroGenFeatureWrapper(Feature<C> parent, Function<C, String> namer) {
        super(parent.configFactory);
        this.parent = parent;
        this.namer = namer;
    }

    private final Feature<C> parent;
    private final Function<C, String> namer;

    @Override
    public boolean place(@Nonnull IWorld worldIn, @Nonnull ChunkGenerator<? extends GenerationSettings> generator, @Nonnull Random rand, @Nonnull BlockPos pos, @Nonnull C config) {
        return parent.place(worldIn, generator, rand, pos, config);
    }

    @Override
    public String getName(C config) {
        return namer.apply(config);
    }

}
