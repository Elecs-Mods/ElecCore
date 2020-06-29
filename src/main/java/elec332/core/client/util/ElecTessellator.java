package elec332.core.client.util;

import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-11-2015.
 */
@OnlyIn(Dist.CLIENT)
public class ElecTessellator implements ITessellator {

    public ElecTessellator() {
        this(Tessellator.getInstance());
    }

    public ElecTessellator(Tessellator tessellator) {
        this.worldRenderer = tessellator.getBuffer();
        this.tessellator = tessellator;
    }

    public ElecTessellator(BufferBuilder worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.tessellator = null;
    }

    private final Tessellator tessellator;
    private final BufferBuilder worldRenderer;
    private VertexBufferConsumer vertexBufferConsumer;
    private int brightness1, brightness2;
    private int color1, color2, color3, color4;

    @Override
    public void setBrightness(int brightness) {
        brightness1 = brightness >> 16 & 65535;
        brightness2 = brightness & 65535;
    }

    @Override
    public void setColorOpaque_F(float f1, float f2, float f3) {
        setColorOpaque((int) (f1 * 255.0F), (int) (f2 * 255.0F), (int) (f3 * 255.0F));
    }

    @Override
    public void setColorOpaque(int i1, int i2, int i3) {
        setColorRGBA(i1, i2, i3, 255);
    }

    @Override
    public void setColorRGBA_F(float f1, float f2, float f3, float f4) {
        this.setColorRGBA((int) (f1 * 255.0F), (int) (f2 * 255.0F), (int) (f3 * 255.0F), (int) (f4 * 255.0F));
    }

    @Override
    public void setColorRGBA_I(int color, int i2) {
        int k = color >> 16 & 255;
        int l = color >> 8 & 255;
        int i1 = color & 255;
        this.setColorRGBA(k, l, i1, i2);
    }

    @Override
    public void setColorRGBA(int i1, int i2, int i3, int i4) {
        this.color1 = i1;
        this.color2 = i2;
        this.color3 = i3;
        this.color4 = i4;
    }

    @Override
    public void startDrawingWorldBlock() {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
    }

    @Override
    public void startDrawingGui() {
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
    }

    @Override
    public void addVertexWithUV(double x, double y, double z, float u, float v) {
        worldRenderer.pos(x, y, z);
        drawColor();
        worldRenderer.tex(u, v); //tex
        worldRenderer.lightmap(brightness1, brightness2); //lightmap
        worldRenderer.endVertex();
    }

    @Nonnull
    @Override
    public BufferBuilder getBuffer() {
        return worldRenderer;
    }

    @Override
    public void draw() {
        if (tessellator == null) {
            RenderHelper.drawBuffer(this.worldRenderer);
            return;
        }
        tessellator.draw();
    }

    @Nonnull
    @Override
    public VertexBufferConsumer getVertexBufferConsumer() {
        if (vertexBufferConsumer == null) {
            vertexBufferConsumer = new VertexBufferConsumer(getBuffer());
        }
        return vertexBufferConsumer;
    }

    private void drawColor() {
        worldRenderer.color(color1, color2, color3, color4);
    }

}
