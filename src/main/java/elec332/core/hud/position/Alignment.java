package elec332.core.hud.position;

import com.google.common.base.Strings;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.hud.drawing.IDrawer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum Alignment {

    LEFT {
        @Override
        public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nullable String display, int x, int y, Object... data) {
            GlStateManager.pushMatrix();
            int width = drawer.draw(toDraw, GuiDraw.mc, this, x, y, data);
            if (!Strings.isNullOrEmpty(display)) {
                RenderHelper.getMCFontrenderer().drawString(display, x + width, y + 5, Color.WHITE.getRGB());
            }
            GlStateManager.popMatrix();
        }

    },
    RIGHT {
        @Override
        public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nullable String display, int x, int y, Object... data) {
            GlStateManager.pushMatrix();
            int width = drawer.draw(toDraw, GuiDraw.mc, this, x, y, data);
            if (!Strings.isNullOrEmpty(display)) {
                FontRenderer fr = RenderHelper.getMCFontrenderer();
                fr.drawString(display, x - width - fr.getStringWidth(display), y + 5, Color.WHITE.getRGB());
            }
            GlStateManager.popMatrix();
        }

    };

    public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, int x, int y, Object... data) {
        this.renderHudPart(drawer, toDraw, null, x, y, data);
    }

    public abstract <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nullable String display, int x, int y, Object... data);

}
