package elec332.core.hud.drawing;

import elec332.core.hud.position.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 8-1-2017.
 */
public class ItemStackDrawer implements IDrawer<ItemStack> {

    private ItemStackDrawer() {
    }

    public static final IDrawer<ItemStack> INSTANCE;

    @Override
    @OnlyIn(Dist.CLIENT)
    public int draw(ItemStack drawable, Minecraft mc, Alignment alignment, int x, int y, Object... data) {
        RenderHelper.enableGUIStandardItemLighting();
        mc.getItemRenderer().renderItemIntoGUI(drawable, x, y);
        RenderHelper.disableStandardItemLighting();
        return 18;
    }

    static {
        INSTANCE = new ItemStackDrawer();
    }

}
