package elec332.core.client.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 3-1-2020
 */
public class WrappedUnbakedModel implements IUnbakedModel {

    public WrappedUnbakedModel(IUnbakedModel base) {
        this.base = base;
    }

    protected final IUnbakedModel base;

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return base.getDependencies();
    }

    @Nonnull
    @Override
    public Collection<Material> getTextures(@Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter, @Nonnull Set<Pair<String, String>> missingTextureErrors) {
        return base.getTextures(modelGetter, missingTextureErrors);
    }

    @Nullable
    @Override
    public IBakedModel bakeModel(@Nonnull ModelBakery modelBakeryIn, @Nonnull Function<Material, TextureAtlasSprite> spriteGetterIn, @Nonnull IModelTransform transformIn, @Nonnull ResourceLocation locationIn) {
        return base.bakeModel(modelBakeryIn, spriteGetterIn, transformIn, locationIn);
    }

}
