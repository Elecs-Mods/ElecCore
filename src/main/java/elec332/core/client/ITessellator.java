package elec332.core.client;

import net.minecraft.client.renderer.Tessellator;

/**
 * Created by Elec332 on 25-11-2015.
 */
public interface ITessellator {

    public void setBrightness(int brightness);

    public void setColorOpaque_F(float f1, float f2, float f3);

    public void setColorOpaque(int i1, int i2, int i3);

    public void setColorRGBA_F(float f1, float f2, float f3, float f4);

    public void setColorRGBA_I(int color, int i2);

    public void setColorRGBA(int i1, int i2, int i3, int i4);

    public void addVertexWithUV(double x, double y, double z, double u, double v);

    public Tessellator getMCTessellator();

}
