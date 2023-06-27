package elec332.core.api.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import com.google.common.collect.Lists;
import elec332.core.util.FMLHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingStage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-10-2016.
 * <p>
 * A wrapper for Forge's configuration with some more features
 */
public interface IConfigWrapper {

    String CATEGORY_GENERAL = "general";
    String TOML_EXTENSION = ".toml";
    String CATEGORY_SPLITTER = ".";

    List<IConfigElementSerializer> serializers = Lists.newArrayList();

    /**
     * Registers a class or object to this {@link IConfigWrapper} in the specified category,
     * all fields/methods annotated with {@link Configurable} will be handled
     *
     * @param o The instance/class of your configuration
     */
    default void registerConfig(Object o, @Nonnull String category, @Nullable String comment) {
        getSubConfig(category, comment).registerConfig(o);
    }

    /**
     * Registers a class or object to this {@link IConfigWrapper},
     * all fields/methods annotated with {@link Configurable} will be handled
     *
     * @param o The instance/class of your configuration
     */
    void registerConfig(Object o);

    /**
     * Registers a class or object to this {@link IConfigWrapper} in the specified category,
     * all fields/methods annotated with {@link Configurable} will be handled.
     * <p>
     * All inner classes will be registered aswell
     *
     * @param obj      The top-level instance/class of your configuration
     * @param category The category the consumer will be run for
     * @param comment  An optional comment for the specified category
     */
    default void registerConfigWithInnerClasses(Object obj, @Nonnull String category, @Nullable String comment) {
        getSubConfig(category, comment).registerConfigWithInnerClasses(obj);
    }

    /**
     * Registers a class or object to this {@link IConfigWrapper},
     * all fields/methods annotated with {@link Configurable} will be handled.
     * <p>
     * All inner classes will be registered aswell
     *
     * @param obj The top-level instance/class of your configuration
     */
    void registerConfigWithInnerClasses(Object obj);

    /**
     * Runs the consumer through the config builder of this {@link IConfigWrapper} in the specified category
     *
     * @param initializer The config initializer
     * @param category    The category the consumer will be run for
     * @param comment     An optional comment for the specified category
     */
    default void initializeConfig(Consumer<ForgeConfigSpec.Builder> initializer, @Nonnull String category, @Nullable String comment) {
        getSubConfig(category, comment).registerConfig(builder -> {
            initializer.accept(builder);
            return null;
        });
    }

    /**
     * Runs the consumer through the config builder of this {@link IConfigWrapper},
     *
     * @param initializer The config initializer
     */
    default void initializeConfig(Consumer<ForgeConfigSpec.Builder> initializer) {
        registerConfig(builder -> {
            initializer.accept(builder);
            return null;
        });
    }

    /**
     * Registers and initializes an object to this {@link IConfigWrapper} in the specified category
     *
     * @param factory  The config factory
     * @param category The category the object will be registered in
     * @param comment  An optional comment for the specified category
     */
    default <T> T registerConfig(Function<ForgeConfigSpec.Builder, T> factory, @Nonnull String category, @Nullable String comment) {
        return getSubConfig(category, comment).registerConfig(factory);
    }

    /**
     * Registers and initializes an object to this {@link IConfigWrapper}
     *
     * @param factory The config factory
     */
    <T> T registerConfig(Function<ForgeConfigSpec.Builder, T> factory);

    /**
     * Registers an {@link IConfigurableElement} to this {@link IConfigWrapper} in the specified category
     *
     * @param configurableElement The reconfigurable element to be registered
     * @param category            The category the reconfigurable element will be registered in
     * @param comment             An optional comment for the specified category
     */
    default void registerConfigurableElement(IConfigurableElement configurableElement, @Nonnull String category, @Nullable String comment) {
        getSubConfig(category, comment).registerConfigurableElement(configurableElement);
    }

    /**
     * Registers an {@link IConfigurableElement} to this {@link IConfigWrapper}
     *
     * @param configurableElement The reconfigurable element to be registered
     */
    void registerConfigurableElement(IConfigurableElement configurableElement);

    /**
     * Used to set a description for the provided configuration category
     *
     * @param category    The category for which the description will be set
     * @param description The description for the provided {@param category}
     * @return The instance of this {@link IConfigWrapper}, so this can be chained.
     */
    @Nonnull
    IConfigWrapper setCategoryDescription(@Nonnull String category, String description);

    /**
     * Configures a nested sub-config in the specified category.
     *
     * @param category     The category the configurator will run in
     * @param configurator The configurator that will be run in the specified category
     */
    default void configureSubConfig(@Nonnull String category, Consumer<IConfigWrapper> configurator) {
        configureSubConfig(category, null, configurator);
    }

    /**
     * Configures a nested sub-config in the specified category.
     *
     * @param category     The category the configurator will run in
     * @param comment      A comment for the specified category
     * @param configurator The configurator that will be run in the specified category
     */
    default void configureSubConfig(@Nonnull String category, String comment, Consumer<IConfigWrapper> configurator) {
        if (configurator == null) {
            return;
        }
        configurator.accept(getSubConfig(category, comment));
    }

    /**
     * Returns a nested sub-config in the specified category.
     *
     * @param category The category this config will reside in
     * @return A nested configuration for the specified category.
     */
    default IConfigWrapper getSubConfig(@Nonnull String category) {
        return getSubConfig(category, null);
    }

    /**
     * Returns a nested sub-config in the specified category.
     *
     * @param category The category this config will reside in
     * @param comment  A comment for the specified category
     * @return A nested configuration for the specified category.
     */
    @Nonnull
    IConfigWrapper getSubConfig(@Nonnull String category, @Nullable String comment);

    /**
     * Registers this config
     */
    void register();

    /**
     * Used to register a listener for when this configuration gets loaded or has changed
     *
     * @param listener The {@link Runnable} that will be run
     */
    void addLoadListener(Runnable listener);

    /**
     * Function to check whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     *
     * @return Whether this {@link IConfigWrapper} has already been loaded from/saved to disk
     */
    boolean hasBeenLoaded();

    /**
     * Used for checking all registered configuration categories
     *
     * @return A {@link Set} of all registered configuration categories
     */
    @Nonnull
    Set<String> getRegisteredCategories();

    /**
     * Returns the raw currently loaded config data (read-only)
     *
     * @return The currently loaded, read-only, raw config data
     */
    UnmodifiableCommentedConfig getRawReadOnlyData();

    static void registerConfigElementSerializer(IConfigElementSerializer serializer) {
        if (FMLHelper.hasReachedState(ModLoadingStage.ENQUEUE_IMC)) {
            throw new RuntimeException("Cannot register config element serializer after PreInit!");
        }
        serializers.add(serializer);
    }

}
