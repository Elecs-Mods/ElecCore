package elec332.core.hud.drawing;

import elec332.core.hud.position.Alignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 8-1-2017.
 */
public class ItemStackDrawer implements IDrawer<ItemStack> {

    private ItemStackDrawer(){
    }

    public static final IDrawer<ItemStack> INSTANCE;

    @Override
    @SideOnly(Side.CLIENT)
    public int draw(ItemStack drawable, Minecraft mc, Alignment alignment, int x, int y, Object... data) {
        RenderHelper.enableGUIStandardItemLighting();
        mc.renderItem.renderItemIntoGUI(drawable, x, y);
        RenderHelper.disableStandardItemLighting();
        return 18;
    }

    static {
        INSTANCE = new ItemStackDrawer();
    }

}
