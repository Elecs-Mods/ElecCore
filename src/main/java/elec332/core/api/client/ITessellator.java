package elec332.core.api.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.pipeline.VertexBufferConsumer;

import javax.annotation.Nonnull;
import java.util.Objects;

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

    void setBrightness(int brightness);

    void setColorOpaque_F(float red, float green, float blue);

    void setColorOpaque(int red, int green, int blue);

    void setColorRGBA_F(float red, float green, float blue, float alpha);

    void setColorRGBA_I(int color, int alpha);

    void setColorRGBA(int red, int green, int blue, int alpha);

    default void setTransformation(@Nonnull MatrixStack matrix) {
        setMatrix(Objects.requireNonNull(matrix).getLast().getMatrix());
    }

    void setMatrix(Matrix4f matrix);

    void clearMatrix();

    void addVertexWithUV(Matrix4f matrix, double x, double y, double z, float u, float v);

    void addVertexWithUV(double x, double y, double z, float u, float v);

    void startDrawingWorldBlock();

    void startDrawingGui();

    void draw();

    @Nonnull
    IVertexBuilder getVertexBuilder();

    @Nonnull
    VertexBufferConsumer getVertexBufferConsumer();

}
