package elec332.core.api.registry;

import java.util.Map;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IDualObjectRegistry<T, V> extends IDualRegister<T, V> {

    @Override
    public boolean register(T t, V v);

    public Map<T, V> getAllRegisteredObjects();

}