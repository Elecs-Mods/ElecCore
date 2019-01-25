package elec332.core.api.config;

import net.minecraftforge.common.ForgeConfigSpec;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 18-10-2016.
 * <p>
 * Used in reconfigurable objects/elements, can be registered to an {@link IConfigWrapper}
 */
public interface IConfigurableElement {

    public void reconfigure(@Nonnull ForgeConfigSpec.Builder config);

}
