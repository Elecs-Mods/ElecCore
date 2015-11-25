package elec332.core.client;

import elec332.core.client.ITesselator;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Created by Elec332 on 25-11-2015.
 */
public class ElecTesselator implements ITesselator {

    public ElecTesselator(){
        tesselator = Tessellator.getInstance();
        worldRenderer = tesselator.getWorldRenderer();
    }

    private final Tessellator tesselator;
    private final WorldRenderer worldRenderer;
    private int brightness1, brightness2;
    private int color1, color2, color3, color4;

    public void setBrightness(int brightness){
        brightness1 = brightness >> 16 & 65535;
        brightness2 = brightness & 65535;
    }

    public void setColorOpaque_F(float f1, float f2, float f3){
        setColorOpaque((int) (f1 * 255.0F), (int) (f2 * 255.0F), (int) (f3 * 255.0F));
    }

    public void setColorOpaque(int i1, int i2, int i3){
        setColorRGBA(i1, i2, i3, 255);
    }

    public void setColorRGBA_F(float f1, float f2, float f3, float f4){
        this.setColorRGBA((int)(f1 * 255.0F), (int)(f2 * 255.0F), (int)(f3 * 255.0F), (int)(f4 * 255.0F));
    }

    public void setColorRGBA_I(int color, int i2){
        int k = color >> 16 & 255;
        int l = color >> 8 & 255;
        int i1 = color & 255;
        this.setColorRGBA(k, l, i1, i2);
    }

    public void setColorRGBA(int i1, int i2, int i3, int i4){
        this.color1 = i1;
        this.color2 = i2;
        this.color3 = i3;
        this.color4 = i4;
    }

    public void startDrawingWorldBlock(){
        worldRenderer.func_181668_a(7, DefaultVertexFormats.BLOCK);
    }

    public void startDrawingGui(){
        worldRenderer.func_181668_a(7, DefaultVertexFormats.field_181707_g);
    }

    public void addVertexWithUV(float x, float y, float z, float u, float v){
        worldRenderer.func_181662_b(x, y, z);
        drawColor();
        worldRenderer.func_181673_a(u, v);
        worldRenderer.func_181671_a(brightness1, brightness2);
        worldRenderer.func_181675_d();
    }

    public Tessellator getTesselator(){
        return tesselator;
    }

    private void drawColor(){
        worldRenderer.func_181669_b(color1, color2, color3, color4);
    }
}
