package elec332.core.compat.jei;

import elec332.core.ElecCore;
import elec332.core.util.RegistryHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 5-1-2020
 */
@JeiPlugin
public class JeiCompat implements IModPlugin {

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(ElecCore.MODID, ElecCore.MODID);
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration subtypeRegistry) {
        for (Item item : RegistryHelper.getItemRegistry()) {
            if (item instanceof IHasSpecialSubtypes) {
                subtypeRegistry.registerSubtypeInterpreter(item, new ISubtypeInterpreter() {

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
