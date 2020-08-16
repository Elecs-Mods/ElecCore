package elec332.core.api.module;

import elec332.core.api.discovery.IAnnotationDataHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Created by Elec332 on 14-10-2016.
 */
public interface IModuleManager {

    /**
     * Gets a set of all current modules, wrapped in their {@link IModuleContainer}
     *
     * @return All modules currently active
     */
    @Nonnull
    Set<IModuleContainer> getActiveModules();

    /**
     * Gets a active module by its name
     *
     * @param module The module name
     * @return The module corresponding to the supplied name
     */
    @Nullable
    IModuleContainer getActiveModule(ResourceLocation module);

    /**
     * Registers a field processor;
     * A field annotated with {@param annotation} will be set with the value supplied by the {@param function}
     *
     * @param annotation The field annotation
     * @param function   The function that will provide the value for the annotated field
     */
    void registerFieldProcessor(Class<? extends Annotation> annotation, Function<IModuleContainer, Object> function);

    /**
     * This function allows other mods to implement their own way of marking/annotating/... modules.
     * The function gives the discoverer an {@link IAnnotationDataHandler} for finding the modules and a function
     * to get the {@link IModuleController} from the supplied mod-id.
     * <p>
     * The function has to return a non-null list of the {@link IModuleInfo}'s it found
     * (an empty list if no modules were found)
     *
     * @param discoverer The module discoverer.
     */
    void registerModuleDiscoverer(BiFunction<IAnnotationDataHandler, Function<String, IModuleController>, List<IModuleInfo>> discoverer);

    /**
     * Invokes an event on all modules currently active
     *
     * @param event The event to be invoked on all modules
     */
    void invokeEvent(Object event);

    /**
     * When an event type gets registered with this function, it will always be passed on to the module, regardless whether it is enabled or not
     * Use this for (custom) events that should always be processed (Example: ElecCore registers Registry events as an unchecked event type)
     *
     * @param eventType The Event type
     */
    void registerUncheckedEventType(Class<? extends Event> eventType);

}
