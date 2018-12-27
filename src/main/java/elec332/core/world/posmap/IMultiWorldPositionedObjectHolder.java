package elec332.core.world.posmap;

import elec332.core.api.util.IClearable;
import elec332.core.world.WorldHelper;
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
public interface IMultiWorldPositionedObjectHolder<T> extends IClearable {

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension,
     * Returns null if it doesn't exist
     *
     * @param world The dimension to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension, can be null
     */
    @Nullable
    default public PositionedObjectHolder<T> get(World world) {
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
    default public PositionedObjectHolder<T> getOrCreate(World world) {
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
    public PositionedObjectHolder<T> get(int world);

    /**
     * Gets the {@link PositionedObjectHolder} for the specified dimension-ID,
     * creates a new one if it doesn't exist
     *
     * @param world The dimension-ID to be checked
     * @return The {@link PositionedObjectHolder} for the specified dimension-ID
     */
    @Nonnull
    public PositionedObjectHolder<T> getOrCreate(int world);

    /**
     * @return A collection of all underlying {@link PositionedObjectHolder}'s
     */
    @Nonnull
    public Collection<PositionedObjectHolder<T>> getValues();

    /**
     * @return A map with all underlying {@link PositionedObjectHolder}'s including the dimension it belongs to
     */
    @Nonnull
    public Map<Integer, PositionedObjectHolder<T>> getUnModifiableView();

    /**
     * Add a callback, gets called when a new {@link PositionedObjectHolder} is created
     *
     * @param callback the callback
     */
    public void addCreateCallback(Consumer<PositionedObjectHolder<T>> callback);

    /**
     * Clears this map, clearing and removing all underlying {@link PositionedObjectHolder}'s
     */
    @Override
    public void clear();

}
