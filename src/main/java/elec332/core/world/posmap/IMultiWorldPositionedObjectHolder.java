package elec332.core.world.posmap;

import elec332.core.world.WorldHelper;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 8-11-2017.
 */
public interface IMultiWorldPositionedObjectHolder<T> {

    @Nullable
    default public PositionedObjectHolder<T> get(World world) {
        return get(WorldHelper.getDimID(world));
    }

    @Nonnull
    default public PositionedObjectHolder<T> getOrCreate(World world) {
        return getOrCreate(WorldHelper.getDimID(world));
    }

    @Nullable
    public PositionedObjectHolder<T> get(int world);

    @Nonnull
    public PositionedObjectHolder<T> getOrCreate(int world);

    @Nonnull
    public Collection<PositionedObjectHolder<T>> getValues();

    @Nonnull
    public Map<Integer, PositionedObjectHolder<T>> getUnModifiableView();

    public void addCreateCallback(Consumer<PositionedObjectHolder<T>> callback);

    public void clear();

}
