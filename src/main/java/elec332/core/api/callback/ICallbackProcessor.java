package elec332.core.api.callback;

import java.util.List;

/**
 * Created by Elec332 on 29-9-2016.
 * <p>
 * A callback processor, every class annotated with {@link RegisteredCallback} will be instantiated and available to the
 * callback processor, which can filter out his callbacks and process them.
 */
public interface ICallbackProcessor {

    /**
     * Used to get the callbacks this class needs to process from the list (and optionally process them in the same function)
     *
     * @param callbacks All (instantiated) classes annotated with {@link RegisteredCallback}
     */
    void getCallbacks(List<?> callbacks);

}
