package elec332.core.api.config;

import net.minecraftforge.common.config.Configuration;

/**
 * Created by Elec332 on 18-10-2016.
 *
 * Used in reconfigurable objects/elements, can be registered to an {@link IConfigWrapper}
 */
public interface IConfigurableElement {

    public void reconfigure(Configuration config);

}
