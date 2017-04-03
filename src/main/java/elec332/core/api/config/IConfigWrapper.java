package elec332.core.api.config;

import net.minecraftforge.common.config.Configuration;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 18-10-2016.
 */
public interface IConfigWrapper {

    public void registerConfig(Object o);

    public void registerConfigWithInnerClasses(Object obj);

    public void registerConfigurableElement(IConfigurableElement configurableElement);

    @Nonnull
    public IConfigWrapper setCategoryData(String category, String description);

    public boolean hasBeenLoaded();

    default public void refresh() {
        refresh(true);
    }

    public void refresh(boolean load);

    @Nonnull
    public List<String> getRegisteredCategories();

    @Nonnull
    public Configuration getConfiguration();

    @Nonnull
    public Configuration wrapCategoryAsConfig(String category);

}
