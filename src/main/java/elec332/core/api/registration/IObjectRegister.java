package elec332.core.api.registration;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Created by Elec332 on 12-10-2017.
 */
public interface IObjectRegister<T extends IForgeRegistryEntry<T>> {

    default public void preRegister(){
    }

    public void register(IForgeRegistry<T> registry);

    public Class<T> getType();

}
