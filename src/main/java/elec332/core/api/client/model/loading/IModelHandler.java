package elec332.core.api.client.model.loading;

import java.util.List;

/**
 * Created by Elec332 on 18-9-2016.
 * <p>
 * Main model handler interface, used to handle
 * (register, make, ...) models for all sorts of things
 */
public interface IModelHandler {

    /**
     * @return Whether this model handler is enabled or not
     */
    default boolean enabled() {
        return true;
    }

    /**
     * Can be used to fetch sub-handlers (annotated with {@link ModelHandler}
     *
     * @param list All objects annotated with {@link ModelHandler}
     */
    default void collectModelHandlers(List<?> list) {
    }

    /**
     * Used to setup handler and prepare for registering the models.
     * <p>
     * Gets called after {@link IModelHandler#collectModelHandlers(List)}
     */
    default void preHandleModels() {
    }

}
