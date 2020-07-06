package elec332.core.client.util;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.client.ITessellator;
import elec332.core.client.RenderHelper;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.Vector4f;
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
        this.matrix = null;
    }

    public ElecTessellator(IVertexBuilder worldRenderer) {
        this.worldRenderer = worldRenderer;
        this.tessellator = null;
        this.matrix = null;
    }

    private final Tessellator tessellator;
    private final IVertexBuilder worldRenderer;
    private VertexBufferConsumer vertexBufferConsumer;
    private int brightness1, brightness2;
    private int color1, color2, color3, color4;
    private Matrix4f matrix;

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
    public void setMatrix(Matrix4f matrix) {
        this.matrix = matrix;
    }

    @Override
    public void clearMatrix() {
        setMatrix(null);
    }

    @Override
    public void startDrawingWorldBlock() {
        if (worldRenderer instanceof BufferBuilder) {
            ((BufferBuilder) worldRenderer).begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void startDrawingGui() {
        if (worldRenderer instanceof BufferBuilder) {
            ((BufferBuilder) worldRenderer).begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void addVertexWithUV(Matrix4f matrix, double x, double y, double z, float u, float v) {
        Vector4f vector4f = new Vector4f((float) x, (float) y, (float) z, 1.0F);
        vector4f.transform(matrix);
        addVertexWithUV_(vector4f.getX(), vector4f.getY(), vector4f.getZ(), u, v);
    }

    @Override
    public void addVertexWithUV(double x, double y, double z, float u, float v) {
        if (matrix == null) {
            addVertexWithUV_(x, y, z, u, v);
        } else {
            addVertexWithUV(matrix, x, y, z, u, v);
        }
    }

    private void addVertexWithUV_(double x, double y, double z, float u, float v) {
        worldRenderer.pos(x, y, z);
        worldRenderer.color(color1, color2, color3, color4);
        worldRenderer.tex(u, v); //tex
        worldRenderer.lightmap(brightness1, brightness2); //lightmap
        worldRenderer.normal(0, 1, 0);
        worldRenderer.endVertex();
    }

    @Nonnull
    @Override
    public IVertexBuilder getVertexBuilder() {
        return worldRenderer;
    }

    @Override
    public void draw() {
        if (tessellator == null) {
            if (worldRenderer instanceof BufferBuilder) {
                RenderHelper.drawBuffer((BufferBuilder) this.worldRenderer);
                return;
            } else {
                throw new UnsupportedOperationException();
            }
        }
        tessellator.draw();
    }

    @Nonnull
    @Override
    public VertexBufferConsumer getVertexBufferConsumer() {
        if (vertexBufferConsumer == null) {
            vertexBufferConsumer = new VertexBufferConsumer(worldRenderer);
        }
        return vertexBufferConsumer;
    }

}
