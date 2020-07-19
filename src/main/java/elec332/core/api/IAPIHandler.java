package elec332.core.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

/**
 * Created by Elec332 on 3-11-2016.
 */
public interface IAPIHandler {

    void inject(@Nonnull Object o, Class<?>... classes);

    @Nullable
    <T> T get(@Nonnull Class<T> type);

    default <T> Optional<T> getOptional(@Nonnull Class<T> type) {
        return Optional.ofNullable(get(type));
    }

}
