package elec332.core.config;

import com.electronwill.nightconfig.core.UnmodifiableCommentedConfig;
import elec332.core.api.config.IConfigWrapper;
import elec332.core.api.config.IConfigurableElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 18-1-2020
 */
public class NestedWrappedConfig extends AbstractConfigWrapper {

    public NestedWrappedConfig(IConfigWrapper parent, String category) {
        this.category = category;
        this.parent = parent;
        this.length = category.split(CATEGORY_SPLITTER).length;
    }

    private final String category;
    private final IConfigWrapper parent;
    private final int length;

    @Override
    public <T> T registerConfig(Function<ForgeConfigSpec.Builder, T> factory) {
        return parent.registerConfig(builder -> {
            builder.push(NestedWrappedConfig.this.category);
            T ret = factory.apply(builder);
            builder.pop(NestedWrappedConfig.this.length);
            return ret;
        });
    }

    @Override
    public void registerConfigurableElement(IConfigurableElement configurableElement) {
        parent.registerConfigurableElement(new IConfigurableElement() {

            @Override
            public void registerProperties(@Nonnull ForgeConfigSpec.Builder builder, ModConfig.Type type) {
                builder.push(NestedWrappedConfig.this.category);
                configurableElement.registerProperties(builder, type);
                builder.pop(NestedWrappedConfig.this.length);
            }

            @Override
            public void load() {
                configurableElement.load();
            }

        });
    }

    @Nonnull
    @Override
    public IConfigWrapper setCategoryDescription(@Nonnull String category, String description) {
        parent.setCategoryDescription(this.category + CATEGORY_SPLITTER + category, description);
        return this;
    }

    @Override
    public void register() {
        this.parent.register();
    }

    @Override
    public void addLoadListener(Runnable listener) {
        this.parent.addLoadListener(listener);
    }

    @Override
    public boolean hasBeenLoaded() {
        return this.parent.hasBeenLoaded();
    }

    @Nonnull
    @Override
    public Set<String> getRegisteredCategories() {
        return this.parent.getRegisteredCategories().stream()
                .filter(s -> s.contains(category))
                .collect(Collectors.toSet());
    }

    @Override
    public UnmodifiableCommentedConfig getRawReadOnlyData() {
        return this.parent.getRawReadOnlyData().get(category);
    }

}
