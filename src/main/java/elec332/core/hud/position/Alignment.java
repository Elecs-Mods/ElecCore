package elec332.core.hud.position;

import com.google.common.base.Strings;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.GuiDraw;
import elec332.core.hud.drawing.IDrawer;
import net.minecraft.client.gui.FontRenderer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

/**
 * Created by Elec332 on 8-1-2017.
 */
public enum Alignment {

    LEFT {
        @Override
        public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nonnull MatrixStack stack, @Nullable String display, int x, int y, Object... data) {
            RenderSystem.pushMatrix();
            int width = drawer.draw(toDraw, GuiDraw.mc, this, x, y, data);
            if (!Strings.isNullOrEmpty(display)) {
                RenderHelper.getMCFontrenderer().drawString(stack, display, x + width, y + 5, Color.WHITE.getRGB());
            }
            RenderSystem.popMatrix();
        }

    },
    RIGHT {
        @Override
        public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nonnull MatrixStack stack, @Nullable String display, int x, int y, Object... data) {
            RenderSystem.pushMatrix();
            int width = drawer.draw(toDraw, GuiDraw.mc, this, x, y, data);
            if (!Strings.isNullOrEmpty(display)) {
                FontRenderer fr = RenderHelper.getMCFontrenderer();
                fr.drawString(stack, display, x - width - fr.getStringWidth(display), y + 5, Color.WHITE.getRGB());
            }
            RenderSystem.popMatrix();
        }

    };

    public <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nonnull MatrixStack stack, int x, int y, Object... data) {
        this.renderHudPart(drawer, toDraw, stack, null, x, y, data);
    }

    public abstract <D> void renderHudPart(IDrawer<D> drawer, D toDraw, @Nonnull MatrixStack stack, @Nullable String display, int x, int y, Object... data);

}
