package elec332.core.world.feature;

import elec332.core.api.config.IConfigurableElement;
import elec332.core.api.world.IFeatureGenerator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.config.Configuration;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 * <p>
 * An {@link IFeatureGenerator} wrapper of a {@link WorldGenerator}, highly configurable
 */
@SuppressWarnings("all")
public class FeatureWorldGenerator implements IFeatureGenerator, IConfigurableElement {

    public static FeatureWorldGenerator wrap(String name, WorldGenerator worldGenerator) {
        return new FeatureWorldGenerator(name, 1, worldGenerator);
    }

    public FeatureWorldGenerator(String name, int times, WorldGenerator worldGenerator) {
        this.worldGenerator = worldGenerator;
        this.name = name;
        this.maxY = 64;
        this.times = times;
        this.multiplier = 1;
        this.generate = true;
    }

    protected final String name;
    protected int times;
    protected WorldGenerator worldGenerator;
    protected int maxY;
    protected boolean generate;
    protected float multiplier;

    @Override
    public String getName() {
        return this.name;
    }

    public String getConfigCategoryName() {
        return getName();
    }

    @Override
    public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {
        boolean ret = false;
        if (generate) {
            BlockPos pos = new BlockPos(chunkX * 16 + random.nextInt(16), random.nextInt(maxY), chunkZ * 16 + random.nextInt(16));
            if (times <= 1) {
                if (random.nextFloat() < multiplier) {
                    ret = worldGenerator.generate(world, random, pos);
                }
            } else {
                for (int i = 0; i < times * multiplier; i++) {
                    ret |= worldGenerator.generate(world, random, pos);
                }
            }
        }
        return ret;
    }

    public FeatureWorldGenerator setTimes(int times) {
        this.times = times;
        return this;
    }

    public FeatureWorldGenerator setMaxY(int maxY) {
        this.maxY = maxY;
        return this;
    }

    public FeatureWorldGenerator setGenerationMultiplier(float multiplier) {
        this.multiplier = multiplier;
        return this;
    }

    public FeatureWorldGenerator setShouldGen(boolean shouldGen) {
        this.generate = shouldGen;
        return this;
    }

    @Override
    public void reconfigure(Configuration config) {
        this.generate = config.getBoolean("shouldGenerate", getConfigCategoryName(), this.generate, "Value which determines whether the feature should generate in the world or not");
        String s = times <= 1 ? "Sets the chance this feature will generate in a chunk." : "Sets how many times the mod will attempt to generate ores per chunk. 1 is normal, 0 is not, 2 is 200%, ect.";
        this.multiplier = config.getFloat("generationMultiplier", getConfigCategoryName(), this.multiplier, 0, 1000, s);
    }

}
