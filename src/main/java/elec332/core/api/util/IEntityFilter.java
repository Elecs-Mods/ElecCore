package elec332.core.api.util;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import net.minecraft.entity.Entity;

import java.util.List;

/**
 * Created by Elec332 on 25-10-2016.
 */
public interface IEntityFilter<P extends Entity> {

    public List<P> filterEntities(List<P> toFilter);

    public static <E extends Entity> IEntityFilter<E> of(Predicate<E> predicate){
        return new IEntityFilter<E>() {
            @Override
            public List<E> filterEntities(List<E> toFilter) {
                List<E> ret = Lists.newArrayList();
                for (E e : toFilter){
                    if (predicate.apply(e)){
                        ret.add(e);
                    }
                }
                return ret;
            }
        };
    }

}
