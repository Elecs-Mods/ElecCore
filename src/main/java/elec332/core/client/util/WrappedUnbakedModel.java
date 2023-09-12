package elec332.core.client.util;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.model.IModelState;

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
        this(base, base.getDefaultState());
    }

    public WrappedUnbakedModel(IUnbakedModel base, IModelState state) {
        this.base = base;
        this.state = state;
    }

    protected final IUnbakedModel base;
    protected final IModelState state;

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return base.getDependencies();
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures(@Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter, @Nonnull Set<String> missingTextureErrors) {
        return base.getTextures(modelGetter, missingTextureErrors);
    }

    @Nullable
    @Override
    public IBakedModel bake(@Nonnull ModelBakery bakery, @Nonnull Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite, @Nonnull VertexFormat format) {
        return base.bake(bakery, spriteGetter, sprite, format);
    }

    @Nonnull
    @Override
    public IModelState getDefaultState() {
        return state;
    }

    @Nonnull
    @Override
    public IUnbakedModel process(ImmutableMap<String, String> customData) {
        return base.process(customData);
    }

    @Nonnull
    @Override
    public IUnbakedModel smoothLighting(boolean value) {
        return base.smoothLighting(value);
    }

    @Nonnull
    @Override
    public IUnbakedModel gui3d(boolean value) {
        return base.gui3d(value);
    }

    @Nonnull
    @Override
    public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
        return base.retexture(textures);
    }

}
