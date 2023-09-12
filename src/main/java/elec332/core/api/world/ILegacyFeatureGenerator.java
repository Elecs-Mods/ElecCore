package elec332.core.api.world;

import net.minecraft.world.IWorld;

import java.util.Random;

/**
 * Created by Elec332 on 31-12-2018
 */
public interface ILegacyFeatureGenerator {

    public String getName();

    public boolean generateFeature(IWorld world, int chunkX, int chunkZ, Random random, boolean retroGen);


}
