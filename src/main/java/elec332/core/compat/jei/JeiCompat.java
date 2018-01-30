package elec332.core.compat.jei;

import elec332.core.util.RegistryHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.ISubtypeRegistry;
import mezz.jei.api.JEIPlugin;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 22-1-2018.
 */
@JEIPlugin
public class JeiCompat implements IModPlugin {

    @Override
    public void registerItemSubtypes(ISubtypeRegistry subtypeRegistry) {
        for (Item item : RegistryHelper.getItemRegistry()){
            if (item instanceof IHasSpecialSubtypes){
                subtypeRegistry.registerSubtypeInterpreter(item, new ISubtypeRegistry.ISubtypeInterpreter() {

                    @Override
                    @Nonnull
                    public String apply(@Nonnull ItemStack itemStack) {
                        return ((IHasSpecialSubtypes) item).getIdentifier(itemStack);
                    }

                });
            }
        }
    }

}
