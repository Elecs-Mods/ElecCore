package elec332.core.inventory.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.client.RenderHelper;
import elec332.core.inventory.window.Window;
import elec332.core.util.StatCollector;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    private float txtSize = 1;
    private boolean center, centerWindow;
    private Supplier<String> txt;

    public WidgetText centerWindowX() {
        this.centerWindow = true;
        return this;
    }

    public WidgetText setTextSize(int txtSize) {
        this.txtSize = (float) txtSize / RenderHelper.getMCFontrenderer().FONT_HEIGHT;
        return this;
    }

    public WidgetText setTextScale(float scale) {
        this.txtSize = scale;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void draw(Window window, int guiX, int guiY, double mouseX, double mouseY, float partialTicks) {
        RenderSystem.pushMatrix();
        RenderSystem.translatef(guiX, guiY, 0);
        FontRenderer font = RenderHelper.getMCFontrenderer();
        String txt = StatCollector.translateToLocal(this.txt.get());
        RenderSystem.scalef(txtSize, txtSize, txtSize);
        int xN = centerWindow ? window.xSize / 2 : x;
        font.drawString(txt, center ? (xN - font.getStringWidth(txt) / 2f) : xN, y, 4210752);
        RenderSystem.color4f(1, 1, 1, 1);
        RenderSystem.popMatrix();
    }

}
