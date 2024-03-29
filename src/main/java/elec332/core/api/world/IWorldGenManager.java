package elec332.core.api.world;

import elec332.core.api.registration.IWorldGenRegister;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.fml.ModContainer;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenManager {

    void registerBlockChangedHook(IWorldEventHook listener);

    void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, Object owner);

    void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, ModContainer owner);

    IBiomeGenWrapper getBiomeRegister(Biome biome);

    Set<Map.Entry<ResourceLocation, Structure<?>>> getRegisteredStructures();

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world             The world the chunk is located in
     * @param chunk             The position of the chunk
     * @param featureGenerators The features to register
     */
    void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, ConfiguredFeature<?, ?>... featureGenerators);

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world             The world the chunk is located in
     * @param chunk             The position of the chunk
     * @param featureGenerators The features to register
     */
    void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, Collection<ConfiguredFeature<?, ?>> featureGenerators);

    /**
     * Registers a Chunk IO hook.
     *
     * @param chunkIOHook The Chunk IO Hook to register
     * @return Whether the Chunk IO Hook was successfully registered.
     */
    boolean register(IChunkIOHook chunkIOHook);

}
