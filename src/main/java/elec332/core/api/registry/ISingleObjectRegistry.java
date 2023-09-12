package elec332.core.api.registry;

import java.util.Set;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface ISingleObjectRegistry<T> extends ISingleRegister<T> {

    @Override
    public boolean register(T t);

    public Set<T> getAllRegisteredObjects();

}