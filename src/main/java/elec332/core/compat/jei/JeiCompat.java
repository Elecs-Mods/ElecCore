package elec332.core.compat.jei;

import elec332.core.ElecCore;
import elec332.core.inventory.window.WindowGui;
import elec332.core.util.RegistryHelper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;

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

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGuiContainerHandler(WindowGui.class, new IGuiContainerHandler<WindowGui>() {

            @Nonnull
            @Override
            public List<Rectangle2d> getGuiExtraAreas(WindowGui window) {
                return window.getGuiExtraAreas();
            }

        });
    }

}
