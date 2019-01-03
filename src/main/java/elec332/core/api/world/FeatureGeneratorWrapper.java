package elec332.core.api.world;

import elec332.core.api.config.IConfigurableElement;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.config.Configuration;

import java.util.Random;

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
    private String genkey = "initial";

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getGenKey() {
        return genkey;
    }

    @Override
    public boolean populateChunk(IChunkGenerator chunkGenerator, IWorld world, Random random, int chunkX, int chunkZ) {
        return generator.generateFeature(world, chunkX, chunkZ, random, false);
    }

    @Override
    public boolean retroGen(Random random, int chunkX, int chunkZ, IWorld world) {
        return generator.generateFeature(world, chunkX, chunkZ, random, true);
    }

    @Override
    public void reconfigure(Configuration config) {
        if (this.generator instanceof IConfigurableElement) {
            ((IConfigurableElement) this.generator).reconfigure(config);
        }
        this.genkey = config.getString("generationKey", getName(), this.genkey, "When this key differs from the key stored in the chunk data, the chunk will be retrogenned.");
    }

}
