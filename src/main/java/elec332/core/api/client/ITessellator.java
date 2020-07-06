package elec332.core.api.client;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 25-11-2015.
 * <p>
 * A tessellator that works like 1.7.10, meaning that
 * it remembers the brightness, opaque and color settings, and that
 * {@link ITessellator#addVertexWithUV(double, double, double, float, float)}
 * can be called for creating vertices
 */
@OnlyIn(Dist.CLIENT)
public interface ITessellator {

    public void setBrightness(int brightness);

    public void setColorOpaque_F(float red, float green, float blue);

    public void setColorOpaque(int red, int green, int blue);

    public void setColorRGBA_F(float red, float green, float blue, float alpha);

    public void setColorRGBA_I(int color, int alpha);

    public void setColorRGBA(int red, int green, int blue, int alpha);

    public void setMatrix(Matrix4f matrix);

    public void clearMatrix();

    public void addVertexWithUV(Matrix4f matrix, double x, double y, double z, float u, float v);

    public void addVertexWithUV(double x, double y, double z, float u, float v);

    public void startDrawingWorldBlock();

    public void startDrawingGui();

    public void draw();

    @Nonnull
    public IVertexBuilder getVertexBuilder();

    @Nonnull
    public VertexBufferConsumer getVertexBufferConsumer();

}
