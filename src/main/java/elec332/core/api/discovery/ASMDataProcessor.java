package elec332.core.api.discovery;

import net.minecraftforge.fml.common.LoaderState;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Elec332 on 7-3-2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ASMDataProcessor {

    /**
     * Valid LoaderStates:
     * Loaderstate.CONSTRUCTING
     * LoaderState.PREINITIALIZATION
     * LoaderState.INITIALIZATION
     * LoaderState.POSTINITIALIZATION
     * LoaderState.AVAILABLE
     *
     * @return The (array of) LoaderState(s) in which to load this.
     */
    public LoaderState[] value();

}
