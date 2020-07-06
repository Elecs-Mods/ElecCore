package elec332.core.client.model.renderer;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import elec332.core.api.client.IRenderMatrix;
import elec332.core.client.model.legacy.CompatRenderMatrix;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 6-7-2020
 */
public class AbstractModel extends Model {

    public AbstractModel() {
        super(null);
        this.models = Lists.newArrayList();
    }

    private final List<ModelRenderer> models;

    @Override
    public void accept(@Nonnull ModelRenderer modelPart) {
        models.add(modelPart);
    }

    public void render(@Nonnull IRenderMatrix matrixStack, int light, int overlayTexture, float r, float g, float b, float a) {
        CompatRenderMatrix compat = CompatRenderMatrix.unWrap(matrixStack);
        render(compat.getStack(), compat.getVertexBuilder(), light, overlayTexture, r, g, b, a);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStackIn, @Nonnull IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        for (ModelRenderer model : models) {
            model.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
        }
    }

}
