package elec332.core.api.config;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * Created by Elec332 on 18-10-2016.
 * <p>
 * A wrapper for Forge's configuration with some more features
 */
public interface IConfigWrapper {

    public static final String CATEGORY_GENERAL = "general";
    public static final String CATEGORY_CLIENT = "client";

    /**
     * Registers a class or object to this {@link IConfigWrapper},
     * all fields/methods annotated with {@link Configurable} will be handled
     *
     * @param o The instance/class of your configuration
     */
    public void registerConfig(Object o);

    /**
     * Registers a class or object to this {@link IConfigWrapper},
     * all fields/methods annotated with {@link Configurable} will be handled.
     * <p>
     * All inner classes will be registered aswell
     *
     * @param obj The top-level instance/class of your configuration
     */
    public void registerConfigWithInnerClasses(Object obj);

    /**
     * Registers an {@link IConfigurableElement} to this {@link IConfigWrapper}
     *
     * @param configurableElement The reconfigurable element to be registered
     */
    public void registerConfigurableElement(IConfigurableElement configurableElement);

    /**
     * Registers an {@link IConfigurableElement} to this {@link IConfigWrapper}, and runs it immediately
     *
     * @param configurableElement The reconfigurable element to be registered
     */
    public void configureNow(IConfigurableElement configurableElement);

    /**
     * Used to set a description for the provided configuration category
     *
     * @param category    The category for which the description will be set
     * @param description The description for the provided {@param category}
     * @return The instance of this {@link IConfigWrapper}, so this can be chained.
     */
    @Nonnull
    public IConfigWrapper setCategoryData(String category, String description);

    public void bake();

    /**
     * Function to check whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     *
     * @return Whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     */
    public boolean hasBeenLoaded();

    /**
     * Refreshes the config values
     */
    public void load();

    /**
     * Used for checking all registered configuration categories
     *
     * @return A {@link Set} of all registered configuration categories
     */
    @Nonnull
    public Set<String> getRegisteredCategories();

}
