package elec332.core.client.model.renderer;

import elec332.core.api.client.IRenderMatrix;
import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-7-2020
 */
public class AbstractModel extends Model {

    public void render(@Nonnull IRenderMatrix matrixStack, int light, int overlayTexture, float r, float g, float b, float a) {
        matrixStack.push();
        matrixStack.scale(0.0625f);
        for (RendererModel model : boxList) {
            model.render(1);
        }
        matrixStack.pop();
    }

}
