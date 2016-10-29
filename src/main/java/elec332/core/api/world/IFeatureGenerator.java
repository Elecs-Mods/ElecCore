package elec332.core.api.world;

import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IFeatureGenerator {

    public String getName();

    public boolean generateFeature(Random random, int chunkX, int chunkZ, World world);

}
