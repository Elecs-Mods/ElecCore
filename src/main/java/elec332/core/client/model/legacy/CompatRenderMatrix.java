package elec332.core.client.model.legacy;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.client.IRenderMatrix;
import elec332.core.api.client.ITextureLocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.math.vector.Quaternion;

/**
 * Created by Elec332 on 6-7-2020
 */
public class CompatRenderMatrix implements IRenderMatrix {

    public static IRenderMatrix wrap(MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        return new CompatRenderMatrix(stack, buffer, light, overlay);
    }

    public static CompatRenderMatrix unWrap(IRenderMatrix renderMatrix) {
        if (!(renderMatrix instanceof CompatRenderMatrix)) {
            throw new IllegalArgumentException();
        }
        return (CompatRenderMatrix) renderMatrix;
    }

    private CompatRenderMatrix(MatrixStack stack, IRenderTypeBuffer buffer, int light, int overlay) {
        this.stack = stack;
        this.buffer = buffer;
        this.light = light;
        this.overlay = overlay;
        this.vertexBuilder = null;
    }

    private final MatrixStack stack;
    private final IRenderTypeBuffer buffer;
    private final int light;
    private final int overlay;
    private IVertexBuilder vertexBuilder;

    @Override
    public void translate(double x, double y, double z) {
        stack.translate(x, y, z);
    }

    @Override
    public void scale(float scaleX, float scaleY, float scaleZ) {
        stack.scale(scaleX, scaleY, scaleZ);
    }

    @Override
    public void rotateDegrees(float angle, float x, float y, float z) {
        angle *= ((float) Math.PI / 180f);
        float f = (float) Math.sin(angle / 2.0F);
        x *= f;
        y *= f;
        z *= f;
        float w = (float) Math.cos(angle / 2.0F);
        stack.rotate(new Quaternion(x, y, z, w));
    }

    @Override
    public void bindTexture(ITextureLocation location) {
        if (!(location instanceof LegacyTextureLocation)) {
            throw new IllegalArgumentException();
        }
        this.vertexBuilder = buffer.getBuffer(((LegacyTextureLocation) location).getRenderType());
    }

    @Override
    public void push() {
        stack.push();
    }

    @Override
    public void pop() {
        stack.pop();
    }

    public MatrixStack getStack() {
        return stack;
    }

    public IVertexBuilder getVertexBuilder() {
        return vertexBuilder;
    }

}
