package elec332.core.api.config;

import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 18-10-2016.
 * <p>
 * A wrapper for Forge's {@link Configuration} with some more features
 */
public interface IConfigWrapper {

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
     * Used to set a description for the provided configuration category
     *
     * @param category    The category for which the description will be set
     * @param description The description for the provided {@param category}
     * @return The instance of this {@link IConfigWrapper}, so this can be chained.
     */
    @Nonnull
    public IConfigWrapper setCategoryData(String category, String description);

    /**
     * Function to check whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     *
     * @return Whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     */
    public boolean hasBeenLoaded();

    /**
     * Refreshes the config values
     */
    default public void refresh() {
        refresh(true);
    }

    /**
     * Refreshes the config values
     *
     * @param load Whether the values should be loaded from the disk
     *             (if false, it will only be saved if changes were made)
     */
    public void refresh(boolean load);

    /**
     * Used for checking all registered configuration categories
     *
     * @return A {@link List} of all registered configuration categories
     */
    @Nonnull
    public List<String> getRegisteredCategories();

    /**
     * Used for working with Forge's {@link Configuration} implementation in the same file     *
     *
     * @return Forge's {@link Configuration} of this {@link IConfigWrapper}
     */
    @Nonnull
    public Configuration getConfiguration();

    /**
     * Creates a new {@link IConfigWrapper} from a configuration category in this {@link IConfigWrapper}
     *
     * @param category The category that a new {@link IConfigWrapper} will be created from
     * @return An {@link IConfigWrapper} from a configuration category in this {@link IConfigWrapper}
     */
    @Nonnull
    public IConfigWrapper wrapCategoryAsConfigWrapper(String category);

    /**
     * Creates a new {@link Configuration} from a configuration category in this {@link IConfigWrapper}
     *
     * @param category The category that a new {@link Configuration} will be created from
     * @return An {@link Configuration} from a configuration category in this {@link IConfigWrapper}
     */
    @Nonnull
    public Configuration wrapCategoryAsConfig(String category);

}
