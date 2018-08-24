package elec332.core.inventory.widget;

import elec332.core.client.RenderHelper;
import elec332.core.inventory.window.Window;
import elec332.core.util.StatCollector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 21-12-2016.
 */
public class WidgetText extends Widget {

    public WidgetText(int x, int y, boolean center, Supplier<String> text) {
        super(x, y, 0, 0, 0, 0);
        this.center = center;
        this.txt = text;
    }

    public WidgetText(int x, int y, boolean center, String text) {
        super(x, y, 0, 0, 0, 0);
        this.center = center;
        this.txt = () -> text;
    }

    private int txtSize = -1;
    private boolean center, centerWindow;
    private Supplier<String> txt;

    public WidgetText centerWindowX() {
        this.centerWindow = true;
        return this;
    }

    public WidgetText setTextSize(int txtSize) {
        this.txtSize = txtSize;
        return this;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void draw(Window window, int guiX, int guiY, int mouseX, int mouseY) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(guiX, guiY, 0);
        FontRenderer font = RenderHelper.getMCFontrenderer();
        int oH = font.FONT_HEIGHT;
        String txt = StatCollector.translateToLocal(this.txt.get());
        if (txtSize > 0) {
            font.FONT_HEIGHT = txtSize;
        }
        int xN = centerWindow ? window.xSize / 2 : x;
        font.drawString(txt, center ? (xN - font.getStringWidth(txt) / 2) : xN, y, 4210752);
        GlStateManager.color(1, 1, 1, 1);
        font.FONT_HEIGHT = oH;
        GlStateManager.popMatrix();
    }

}
