package elec332.core.world;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 8-11-2017.
 */
public class DefaultMultiWorldPositionedObjectHolder<T> implements IMultiWorldPositionedObjectHolder<T>, Supplier<IMultiWorldPositionedObjectHolder<T>> {

	public DefaultMultiWorldPositionedObjectHolder(Consumer<PositionedObjectHolder<T>> callback){
		this();
		callbacks.add(callback);
	}

	public DefaultMultiWorldPositionedObjectHolder(){
		this.objectsInternal = new Int2ObjectArrayMap<>();
		this.view = Collections.unmodifiableMap(this.objectsInternal);
		this.callbacks = Lists.newArrayList();
	}

	private final List<Consumer<PositionedObjectHolder<T>>> callbacks;
	private final Int2ObjectMap<PositionedObjectHolder<T>> objectsInternal;
	private final Map<Integer, PositionedObjectHolder<T>> view;

	@Nullable
	@Override
	public PositionedObjectHolder<T> get(int world) {
		return objectsInternal.get(world);
	}

	@Nonnull
	@Override
	public PositionedObjectHolder<T> getOrCreate(int world) {
		PositionedObjectHolder<T> ret = get(world);
		if (ret == null){
			ret = create();
			for (Consumer<PositionedObjectHolder<T>> callback : callbacks){
				callback.accept(ret);
			}
			objectsInternal.put(world, ret);
		}
		return ret;
	}

	@Nonnull
	@Override
	public Collection<PositionedObjectHolder<T>> getValues() {
		return objectsInternal.values();
	}

	@Nonnull
	@Override
	public Map<Integer, PositionedObjectHolder<T>> getUnModifiableView() {
		return view;
	}

	@Override
	public void addCreateCallback(Consumer<PositionedObjectHolder<T>> callback) {
		callbacks.add(callback);
	}

	@Nonnull
	protected PositionedObjectHolder<T> create(){
		return new PositionedObjectHolder<>();
	}

	@Override
	public IMultiWorldPositionedObjectHolder<T> get() {
		return this;
	}

}
