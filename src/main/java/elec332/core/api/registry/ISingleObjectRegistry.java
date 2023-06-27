package elec332.core.api.registry;

import java.util.Set;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface ISingleObjectRegistry<T> extends ISingleRegister<T> {

    @Override
    boolean register(T t);

    Set<T> getAllRegisteredObjects();

}