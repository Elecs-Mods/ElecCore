package elec332.core.main;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Sets;
import elec332.core.grid.v2.AbstractGridHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Elec332 on 1-8-2016.
 */
@SuppressWarnings("all")
public final class ElecCoreRegistrar {

    private ElecCoreRegistrar(){
        throw new IllegalAccessError();
    }

    public static final Registry<AbstractGridHandler> GRIDS_V2;


    static {
        IN_MOD_LOADING = input -> !Loader.instance().hasReachedState(LoaderState.AVAILABLE);

        GRIDS_V2 = new Registry<AbstractGridHandler>(ElecCoreRegistrar.IN_MOD_LOADING);
    }

    private static final Predicate IN_MOD_LOADING;

    @SuppressWarnings("all")
    public static class Registry<T> {

        private Registry(){
            this(Predicates.alwaysTrue());
        }

        private Registry(Predicate<T> predicate){
            this.type = Sets.newHashSet();
            this.immutableSet = Collections.unmodifiableSet(type);
            this.predicate = predicate;
        }

        private final Set<T> type, immutableSet;
        private final Predicate<T> predicate;

        public boolean register(T t){
            return predicate.apply(t) && type.add(t);
        }

        public Set<T> getAllRegisteredObjects(){
            return immutableSet;
        }

    }

}
