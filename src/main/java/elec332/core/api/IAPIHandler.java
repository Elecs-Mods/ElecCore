package elec332.core.api;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 3-11-2016.
 */
public interface IAPIHandler {

    public void inject(Object o, Class<?>... classes);

    @Nullable
    public <T> T get(Class<T> type);

}
