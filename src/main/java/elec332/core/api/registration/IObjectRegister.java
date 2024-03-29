package elec332.core.api.registration;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Created by Elec332 on 12-10-2017.
 * <p>
 * Can be used to register objects to an {@link IForgeRegistry},
 * this uses the normal forge events
 */
public interface IObjectRegister<T extends IForgeRegistryEntry<T>> {

    default void preRegister() {
    }

    void register(IForgeRegistry<T> registry);

}
