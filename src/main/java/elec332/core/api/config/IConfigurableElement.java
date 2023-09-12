package elec332.core.api.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 18-10-2016.
 * <p>
 * Used in reconfigurable objects/elements, can be registered to an {@link IConfigWrapper}
 */
public interface IConfigurableElement {

    public void registerProperties(@Nonnull ForgeConfigSpec.Builder config, ModConfig.Type type);

    /**
     * Gets called when the config file is initially loaded
     * and when the config values have changed.
     * <p>
     * Use this to reload e.g. fields with the values from the defined properties
     */
    public default void load() {
    }

}
