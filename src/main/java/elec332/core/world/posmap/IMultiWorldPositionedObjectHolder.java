package elec332.core.world.posmap;

import elec332.core.api.util.IClearable;
import elec332.core.world.WorldHelper;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 8-11-2017.
 * <p>
 * A {@link PositionedObjectHolder} spanning multiple dimensions
 */
public interface IMultiWorldPositionedObjectHolder<T, V> extends IClearable {

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension,
     * Returns null if it doesn't exist
     *
     * @param world The dimension to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension, can be null
     */
    @Nullable
    default PositionedObjectHolder<T, V> get(IWorld world) {
        return get(WorldHelper.getDimID(world));
    }

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension,
     * creates a new one if it doesn't exist
     *
     * @param world The dimension to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension
     */
    @Nonnull
    default PositionedObjectHolder<T, V> getOrCreate(IWorld world) {
        return getOrCreate(WorldHelper.getDimID(world));
    }

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension-ID,
     * Returns null if it doesn't exist
     *
     * @param world The dimension-ID to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension-ID, can be null
     */
    @Nullable
    PositionedObjectHolder<T, V> get(RegistryKey<World> world);

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension-ID,
     * creates a new one if it doesn't exist
     *
     * @param world The dimension-ID to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension-ID
     */
    @Nonnull
    PositionedObjectHolder<T, V> getOrCreate(RegistryKey<World> world);

    /**
     * @return A collection of all underlying {@link PositionedObjectHolder}'s
     */
    @Nonnull
    Collection<PositionedObjectHolder<T, V>> getValues();

    /**
     * @return A map with all underlying {@link PositionedObjectHolder}'s including the dimension it belongs to
     */
    @Nonnull
    Map<RegistryKey<World>, PositionedObjectHolder<T, V>> getUnModifiableView();

    /**
     * Add a callback, gets called when a new {@link PositionedObjectHolder} is created
     *
     * @param callback the callback
     */
    void addCreateCallback(Consumer<PositionedObjectHolder<T, V>> callback);

    /**
     * Clears this map, clearing and removing all underlying {@link PositionedObjectHolder}'s
     */
    @Override
    void clear();

}
