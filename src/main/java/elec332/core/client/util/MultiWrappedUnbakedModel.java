package elec332.core.client.util;

import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Elec332 on 3-1-2020
 */
public class MultiWrappedUnbakedModel extends VariantList implements IUnbakedModel {

    public MultiWrappedUnbakedModel(VariantList variants) {
        this(variants.getVariantList());
    }

    public MultiWrappedUnbakedModel(List<Variant> variants) {
        super(variants);
    }

    @Nullable
    @Override
    public final IBakedModel bakeModel(@Nonnull ModelBakery modelBakery, @Nonnull Function<RenderMaterial, TextureAtlasSprite> spriteGetter, @Nonnull IModelTransform transform, @Nonnull ResourceLocation location) {
        List<Variant> states = getVariantList();
        if (states.isEmpty()) {
            return null;
        }
        if (states.size() == 1) {
            Variant variant = states.get(0);
            ResourceLocation modelLoc = variant.getModelLocation();
            return bakeModel(modelBakery.getUnbakedModel(modelLoc), 0, modelBakery, spriteGetter, variant, modelLoc);
        }
        WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
        for (int i = 0; i < states.size(); i++) {
            Variant variant = states.get(i);
            ResourceLocation modelLoc = variant.getModelLocation();
            IBakedModel bModel = bakeModel(modelBakery.getUnbakedModel(modelLoc), i, modelBakery, spriteGetter, variant, modelLoc);
            builder.add(bModel, variant.getWeight());
        }
        return builder.build();
    }

    protected IBakedModel bakeModel(IUnbakedModel model, int index, @Nonnull ModelBakery modelBakery, @Nonnull Function<RenderMaterial, TextureAtlasSprite> spriteGetter, @Nonnull IModelTransform transform, @Nonnull ResourceLocation modelLocation) {
        return modelBakery.getBakedModel(modelLocation, transform, spriteGetter);
    }

}
