package elec332.core.api.world;

import elec332.core.api.config.IConfigurableElement;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 16-10-2016.
 * <p>
 * Wraps an {@link IFeatureGenerator} to an {@link IAdvancedChunkPopulator},
 * which allows for some extra features.
 */
public class FeatureGeneratorWrapper implements IAdvancedChunkPopulator, IConfigurableElement {

    public FeatureGeneratorWrapper(ILegacyFeatureGenerator generator) {
        this.generator = generator;
        this.name = generator.getName();
    }

    private final ILegacyFeatureGenerator generator;
    private final String name;
    private Supplier<String> genkey = () -> INITIAL;

    private static final String INITIAL = "initial";

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getGenKey() {
        return genkey.get();
    }

    @Override
    public boolean populateChunk(ChunkGenerator chunkGenerator, IWorld world, Random random, int chunkX, int chunkZ) {
        return generator.generateFeature(world, chunkX, chunkZ, random, false);
    }

    @Override
    public boolean retroGen(Random random, int chunkX, int chunkZ, IWorld world) {
        return generator.generateFeature(world, chunkX, chunkZ, random, true);
    }

    @Override
    public void reconfigure(@Nonnull ForgeConfigSpec.Builder config) {
        if (this.generator instanceof IConfigurableElement) {
            ((IConfigurableElement) this.generator).reconfigure(config);
        }
        //category = "generationKey"
        this.genkey = config.comment("When this key differs from the key stored in the chunk data, the chunk will be retrogenned.").define(getName(), INITIAL)::get;
    }

}
