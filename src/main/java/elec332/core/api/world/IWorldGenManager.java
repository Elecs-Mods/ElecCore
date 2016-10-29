package elec332.core.api.world;

import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * Created by Elec332 on 17-10-2016.
 */
public interface IWorldGenManager {

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world The world the chunk is located in
     * @param chunk The position of the chunk
     * @param featureGenerators The IFeatureGenerators to register
     */
    public void registerForRetroGen(@Nonnull World world, @Nonnull ChunkPos chunk, IFeatureGenerator... featureGenerators);

    /**
     * Enqueues one or multiple IFeatureGenerators for retrogen.
     * The WorldGenManager will process this request as soon as possible,
     * without causing any tick issues when a lot of IFeatureGenerators are
     * enqueued at once.
     *
     * @param world The world the chunk is located in
     * @param chunk The position of the chunk
     * @param featureGenerators The IFeatureGenerators to register
     */
    public void registerForRetroGen(@Nonnull World world, @Nonnull ChunkPos chunk, Collection<IFeatureGenerator> featureGenerators);


    /**
     * Registers a FeatureGenerator.
     *
     * @param featureGenerator The Feature Manager to register
     * @return Whether the FeatureGenerator was successfully registered.
     */
    public boolean register(IFeatureGenerator featureGenerator);

    /**
     * Registers a ChunkPopulator.
     *
     * @param chunkPopulator The Chunk Populator to register
     * @return Whether the ChunkPopulator was successfully registered.
     */
    public boolean register(IAdvancedChunkPopulator chunkPopulator);

    /**
     * Registers a WorldGenHook.
     *
     * @param worldGenHook The WorldGen Hook to register
     * @return Whether the WorldGenHook was successfully registered.
     */
    public boolean register(IWorldGenHook worldGenHook);

}
