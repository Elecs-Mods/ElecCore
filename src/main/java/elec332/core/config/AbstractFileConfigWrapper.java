package elec332.core.config;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.config.IConfigurableElement;
import elec332.core.util.function.FuncHelper;
import elec332.core.util.function.UnsafeRunnable;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-1-2020
 */
public abstract class AbstractFileConfigWrapper extends AbstractConfigWrapper {

    public AbstractFileConfigWrapper() {
        this.configuration = new ElecConfigBuilder();
        this.category = Sets.newHashSet();
        this.configurableElements = Lists.newArrayList();
        this.loadTasks = Lists.newArrayList();
    }

    private ElecConfigBuilder configuration;
    private ForgeConfigSpec spec = null;
    private Set<String> category;
    private List<IConfigurableElement> configurableElements;
    private List<Runnable> loadTasks;
    private boolean blockLoad = false;

    protected ForgeConfigSpec getSpec() {
        return spec;
    }

    protected void runLoadTasks() {
        loadTasks.forEach(Runnable::run);
    }

    protected boolean blockLoad() {
        return blockLoad;
    }

    @Nonnull
    @Override
    public AbstractFileConfigWrapper setCategoryDescription(@Nonnull String category, String description) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot set category data after registering!");
        }
        useBuilder(builder -> {
            if (!Strings.isNullOrEmpty(description)) {
                builder.comment(description);
            }
            builder.push(Preconditions.checkNotNull(category));
            return null;
        });
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
    public void register() {
        configurableElements.forEach(this::registerProperties);
        configurableElements = null;
        blockLoad = true;
        spec = configuration.build();
        category = ImmutableSet.copyOf(category);
        configuration = null;
        registerConfigSpec();
        blockLoad = false;
        postRegister();
    }

    protected abstract void registerConfigSpec();

    protected abstract void postRegister();

    protected abstract ModConfig.Type getConfigType();

    @Override
    public boolean hasBeenLoaded() {
        return configuration == null;
    }

    @Override
    public <T> T registerConfig(Function<ForgeConfigSpec.Builder, T> factory) {
        return useBuilder(factory);
    }

    @Override
    public void registerConfigurableElement(IConfigurableElement configurableElement) {
        if (hasBeenLoaded()) {
            throw new RuntimeException("Cannot register config elements after registering!");
        }
        this.configurableElements.add(configurableElement);
        checkReloadListener(configurableElement);
    }

    @Override
    public void addLoadListener(Runnable listener) {
        loadTasks.add(listener);
    }

    private void registerProperties(IConfigurableElement configurableElement) {
        useBuilder(builder -> {
            configurableElement.registerProperties(builder, getConfigType());
            return null;
        });
    }

    private <T> T useBuilder(Function<ForgeConfigSpec.Builder, T> user) {
        int i = configuration.depth;
        T ret = user.apply(configuration);
        if (configuration.depth != i) {
            configuration.pop(configuration.depth - i);
        }
        if (configuration.depth != i) {
            throw new RuntimeException("Invalid config depth!");
        }
        return ret;
    }

    private void checkReloadListener(IConfigurableElement cfg) {
        if (cfg instanceof Runnable) {
            addLoadListener((Runnable) cfg);
        } else if (cfg instanceof UnsafeRunnable) {
            addLoadListener(FuncHelper.safeRunnable((UnsafeRunnable) cfg));
        }
        addLoadListener(cfg::load);
    }

    private static class ElecConfigBuilder extends ForgeConfigSpec.Builder {

        private int depth = 0;

        @Override
        public ForgeConfigSpec.Builder push(List<String> path) {
            depth += path.size();
            return super.push(path);
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

}
