package elec332.core.api.client;

/**
 * Created by Elec332 on 1-7-2020
 */
public interface IRenderMatrix {

    void translate(double x, double y, double z);

    default void scale(float scale) {
        scale(scale, scale, scale);
    }

    void scale(float scaleX, float scaleY, float scaleZ);

    void rotateDegrees(float angle, float x, float y, float z);

    void bindTexture(ITextureLocation location);

    void push();

    void pop();

    default int getLight() {
        return 15728880;
    }

    default int getOverlay() {
        return 0;
    }

}
