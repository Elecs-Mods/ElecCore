package elec332.core.util.function;

import java.util.Objects;

/**
 * Created by Elec332 on 28-7-2020
 */
public interface TriConsumer<A, B, C> {

    void accept(A k, B v, C s);

    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);
        return (a, b, c) -> {
            accept(a, b, c);
            after.accept(a, b, c);
        };
    }

}
