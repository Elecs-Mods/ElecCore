package elec332.core.client.model.renderer;

import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 6-7-2020
 */
public class ElecModelRenderer extends ModelRenderer {

    public ElecModelRenderer(Model model) {
        super(model);
    }

    public ElecModelRenderer(Model model, int texOffX, int texOffY) {
        super(model, texOffX, texOffY);
    }

    @Nonnull
    @Override
    public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ModelRenderer addBox(float x, float y, float z, float width, float height, float depth, boolean mirrorIn) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @Override
    public ElecModelRenderer setTextureSize(int textureWidthIn, int textureHeightIn) {
        super.setTextureSize(textureWidthIn, textureHeightIn);
        return this;
    }

    @Nonnull
    public ElecModelRenderer addBoxLegacy(String partName, float x, float y, float z, int width, int height, int depth, float delta, int texX, int texY) {
        super.addBox(partName, x, y, z, width, height, depth, delta, texX, texY);
        return this;
    }

    @Nonnull
    public ElecModelRenderer addBoxLegacy(float x, float y, float z, int width, int height, int depth) {
        super.addBox(x, y, z, width, height, depth);
        return this;
    }

    @Nonnull
    public ElecModelRenderer addBoxLegacy(float x, float y, float z, int width, int height, int depth, boolean mirrorIn) {
        super.addBox(x, y, z, width, height, depth, mirrorIn);
        return this;
    }

}
