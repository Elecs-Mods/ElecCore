package elec332.core.api.util;

import net.minecraft.entity.Entity;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 25-10-2016.
 * <p>
 * Entity filter
 */
public interface IEntityFilter<P extends Entity> {

    List<P> filterEntities(List<P> toFilter);

    static <E extends Entity> IEntityFilter<E> of(Predicate<E> predicate) {
        return toFilter -> toFilter.stream().filter(predicate).collect(Collectors.toList());
    }

}
