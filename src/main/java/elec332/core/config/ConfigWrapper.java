package elec332.core.config;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.config.Configurable;
import elec332.core.api.config.IConfigElementSerializer;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import elec332.core.util.ReflectionHelper;
import elec332.core.util.function.FuncHelper;
import elec332.core.util.function.UnsafeRunnable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingStage;

import javax.annotation.Nonnull;
import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 12-4-2015.
 */
public class ConfigWrapper implements IConfigWrapper {

    public ConfigWrapper(File location) {
        this(location.toPath());
    }

    public ConfigWrapper(Path location) {
        this(CommentedFileConfig.builder(location).sync().autosave().writingMode(WritingMode.REPLACE).build());
    }

    public ConfigWrapper(CommentedConfig cfg) {
        this.configuration = new ElecConfigBuilder();
        this.instances = Lists.newArrayList();
        this.category = Sets.newHashSet();
        this.configurableElements = Lists.newArrayList();
        this.loadTasks = Lists.newArrayList();
        this.cfgBuilder = cfg;
    }

    private CommentedConfig cfgBuilder;
    private ElecConfigBuilder configuration;
    private ForgeConfigSpec spec = null;
    private List<Object> instances;
    private Set<String> category;
    private List<IConfigurableElement> configurableElements;
    private List<Runnable> loadTasks;
    private boolean blockLoad = false;
    private FileConfig file = null;
    private static List<IConfigElementSerializer> serializers;


    @Override
    public void registerConfig(Object o) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot register configs after baking!");
        }
        if (instances.contains(o)) {
            return;
        }
        this.instances.add(o);
        Class objClass = o.getClass();
        if (o instanceof Class) {
            objClass = (Class) o;
            o = null;
        }
        final Object fInst = o;
        String classCategory = CATEGORY_GENERAL;
        String comment = "";
        if (objClass.isAnnotationPresent(Configurable.Class.class)) {
            Configurable.Class configClass = (Configurable.Class) objClass.getAnnotation(Configurable.Class.class);
            if (configClass.inherit()) {
                Class[] classes = ReflectionHelper.getAllTillMainClass(objClass);
                StringBuilder s = new StringBuilder();
                for (Class clazz : classes) {
                    if (clazz.isAnnotationPresent(Configurable.Class.class)) {
                        if (s.length() != 0) {
                            s.append(".");
                        }
                        String s1 = ((Configurable.Class) clazz.getAnnotation(Configurable.Class.class)).category();
                        s.append(Strings.isNullOrEmpty(s1) ? clazz.getSimpleName() : s1);
                    }
                }
                classCategory = s.toString();
            } else {
                classCategory = configClass.category();
            }
            comment = configClass.comment();
        }
        for (Field field : objClass.getDeclaredFields()) {
            try {
                boolean oldAccess = field.isAccessible();
                field.setAccessible(true);
                if (field.isAnnotationPresent(Configurable.class)) {
                    Configurable configurable = field.getAnnotation(Configurable.class);
                    Object oldValue = field.get(fInst);
                    String category = configurable.category();
                    if (Strings.isNullOrEmpty(category)) {
                        category = classCategory;
                    }
                    boolean serialized = false;
                    int i = configuration.depth;
                    if (!Strings.isNullOrEmpty(comment)) {
                        configuration.comment(comment);
                    }
                    configuration.push(category);
                    for (IConfigElementSerializer serializer : serializers) {
                        ForgeConfigSpec.ConfigValue cv = serializer.makeConfigEntry(field.getType(), o, field, configurable, configuration, oldValue, configurable.comment());
                        if (cv != null) {
                            loadTasks.add(FuncHelper.safeRunnable(() -> field.set(fInst, cv.get())));
                            configuration.pop(configuration.depth - i);
                            serialized = true;
                            break;
                        }
                    }
                    if (!serialized) {
                        throw new RuntimeException("Could not find serializer for type " + field.getType());
                    }
                    if (configuration.depth != i) {
                        throw new RuntimeException("Invalid config depth!");
                    }
                }
                field.setAccessible(oldAccess);
            } catch (RuntimeException e) {
                throw e; //Eh
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        /* todo: Maybe re-implement?
        for (Method method : objClass.getDeclaredMethods()) {
            try {
                boolean oldAccess = method.isAccessible();
                method.setAccessible(true);
                if (method.isAnnotationPresent(Configurable.class) && method.getParameterTypes().length == 0) {
                    Configurable configurable = method.getAnnotation(Configurable.class);
                    if (configuration.getBoolean(method.getName(), configurable.category(), configurable.enabledByDefault(), configurable.comment())) {
                        method.invoke(o);
                    }
                }

                method.setAccessible(oldAccess);
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
         */
    }

    @Nonnull
    @Override
    public ConfigWrapper setCategoryData(String category, String description) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot set category data after baking!");
        }
        int i = configuration.depth;
        if (!Strings.isNullOrEmpty(description)) {
            configuration.comment(description);
        }
        configuration.push(category);
        configuration.pop(configuration.depth - i);
        this.category.add(category);
        return this;
    }

    @Nonnull
    @Override
    public Set<String> getRegisteredCategories() {
        if (hasBeenLoaded()) {
            return ImmutableSet.copyOf(category);
        }
        return category;
    }

    @Override
    public void bake() {
        configurableElements.forEach(ce -> ce.reconfigure(configuration));
        configurableElements = null;
        blockLoad = true;
        spec = configuration.build();
        spec.setConfig(cfgBuilder);
        category = ImmutableSet.copyOf(category);
        configuration = null;
        try {
            if (file != null && hasAutoReload.test(file.getFile().toPath())) {
                FileWatcher.defaultInstance().setWatch(file.getFile(), this::load);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        blockLoad = false;
        load();
    }

    @Override
    public boolean hasBeenLoaded() {
        return configuration == null;
    }

    @Override
    public void registerConfigWithInnerClasses(Object obj) {
        registerConfig(obj);
        for (Class<?> clazz : Lists.reverse(Lists.newArrayList(obj.getClass().getDeclaredClasses()))) {
            if (!clazz.isInterface()) {
                try {
                    registerConfigWithInnerClasses(clazz.getConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Error registering config: " + clazz.getName());
                }
            }
        }
    }

    @Override
    public void registerConfigurableElement(IConfigurableElement configurableElement) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot register config elements after baking!");
        }
        this.configurableElements.add(configurableElement);
        checkReloadListener(configurableElement);
    }

    @Override
    public void configureNow(IConfigurableElement configurableElement) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot register config elements after baking!");
        }
        configurableElement.reconfigure(configuration);
        checkReloadListener(configurableElement);
    }

    @Override
    public void load() {
        if (spec == null) {
            throw new IllegalStateException();
        }
        if (blockLoad) {
            return;
        }
        if (file != null) {
            file.load();
        }
        loadTasks.forEach(Runnable::run);
    }

    private void checkReloadListener(IConfigurableElement cfg) {
        if (cfg instanceof Runnable) {
            loadTasks.add((Runnable) cfg);
        } else if (cfg instanceof UnsafeRunnable) {
            loadTasks.add(FuncHelper.safeRunnable((UnsafeRunnable) cfg));
        }
    }

    private class ElecConfigBuilder extends ForgeConfigSpec.Builder {

        private int depth = 0;

        @Override
        public <T> ForgeConfigSpec.ConfigValue<T> define(List<String> path, ForgeConfigSpec.ValueSpec value, Supplier<T> defaultSupplier) {
            depth += path.size();
            return super.define(path, value, defaultSupplier);
        }

        @Override
        public ForgeConfigSpec.Builder pop(int count) {
            super.pop(count);
            depth -= count;
            if (depth < 0) {
                throw new RuntimeException();
            }
            return this;
        }

    }

    public static void registerConfigElementSerializer(IConfigElementSerializer serializer) {
        if (FMLHelper.hasReachedState(ModLoadingStage.ENQUEUE_IMC)) {
            throw new RuntimeException("Cannot register config element serializer after PreInit!");
        }
        serializers.add(serializer);
    }

    private static final Predicate<Path> hasAutoReload;

    static {
        serializers = Lists.newArrayList();
        final FieldPointer<FileWatcher, Map> watchedFiles = new FieldPointer<>(FileWatcher.class, "watchedFiles");
        hasAutoReload = p -> watchedFiles.get(FileWatcher.defaultInstance()).containsKey(p.toAbsolutePath());
    }

}
