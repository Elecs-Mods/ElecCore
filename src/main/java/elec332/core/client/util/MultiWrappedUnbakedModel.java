package elec332.core.client.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.Attributes;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.MultiModelState;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 3-1-2020
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class MultiWrappedUnbakedModel implements IUnbakedModel {

    public MultiWrappedUnbakedModel(ModelLoader modelLoader, VariantList variants) {
        this(modelLoader, variants.getVariantList());
    }

    public MultiWrappedUnbakedModel(ModelLoader modelLoader, List<Variant> variants) {
        this(variants.stream()
                        .map(v -> Pair.of(Preconditions.checkNotNull(modelLoader.getUnbakedModel(v.getModelLocation())), v.getState()))
                        .collect(Collectors.toList()),
                variants.stream()
                        .map(Variant::getWeight)
                        .collect(Collectors.toList())
        );
    }

    protected MultiWrappedUnbakedModel(List<Pair<IUnbakedModel, IModelState>> states, List<Integer> weights) {
        this.states = ImmutableList.copyOf(states);
        this.modelState = new MultiModelState(this.states);
        this.weights = weights;
    }

    protected final ImmutableList<Pair<IUnbakedModel, IModelState>> states;
    protected final List<Integer> weights;
    protected final IModelState modelState;

    @Nonnull
    @Override
    public Collection<ResourceLocation> getDependencies() {
        return states.stream()
                .map(Pair::getLeft)
                .map(IUnbakedModel::getDependencies)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Nonnull
    @Override
    public Collection<ResourceLocation> getTextures(@Nonnull Function<ResourceLocation, IUnbakedModel> modelGetter, @Nonnull Set<String> missingTextureErrors) {
        return states.stream()
                .map(Pair::getLeft)
                .map(ubm -> ubm.getTextures(modelGetter, missingTextureErrors))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public IBakedModel bake(@Nonnull ModelBakery bakery, @Nonnull Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite, @Nonnull VertexFormat format) {
        if (!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT)) {
            throw new IllegalArgumentException("Can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
        }
        if (states.size() == 1) {
            Pair<IUnbakedModel, IModelState> data = states.get(0);
            IUnbakedModel model = data.getLeft();
            return bakeModel(model, 0, bakery, spriteGetter, new BasicState(data.getRight(), sprite.isUvLock()), format);
        }

        WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
        for (int i = 0; i < states.size(); i++) {
            Pair<IUnbakedModel, IModelState> data = states.get(i);
            IUnbakedModel model = data.getLeft();
            IBakedModel bModel = bakeModel(model, i, bakery, spriteGetter, new BasicState(MultiModelState.getPartState(data.getRight(), model, i), sprite.isUvLock()), format);
            builder.add(bModel, getWeight(i));
        }
        return builder.build();
    }

    protected IBakedModel bakeModel(IUnbakedModel model, int index, @Nonnull ModelBakery bakery, @Nonnull Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite, @Nonnull VertexFormat format) {
        return model.bake(bakery, spriteGetter, sprite, format);
    }

    protected int getWeight(int model) {
        return weights.size() > model ? weights.get(model) : 1;
    }

    @Nonnull
    @Override
    public IModelState getDefaultState() {
        return modelState;
    }

    @Nonnull
    @Override
    public IUnbakedModel process(ImmutableMap<String, String> customData) {
        return new MultiWrappedUnbakedModel(states.stream()
                .map(p -> Pair.of(p.getLeft().process(customData), p.getRight()))
                .collect(Collectors.toList()), weights);
    }

    @Nonnull
    @Override
    public IUnbakedModel smoothLighting(boolean value) {
        return new MultiWrappedUnbakedModel(states.stream()
                .map(p -> Pair.of(p.getLeft().smoothLighting(value), p.getRight()))
                .collect(Collectors.toList()), weights);
    }

    @Nonnull
    @Override
    public IUnbakedModel gui3d(boolean value) {
        return new MultiWrappedUnbakedModel(states.stream()
                .map(p -> Pair.of(p.getLeft().gui3d(value), p.getRight()))
                .collect(Collectors.toList()), weights);
    }

    @Nonnull
    @Override
    public IUnbakedModel retexture(ImmutableMap<String, String> textures) {
        return new MultiWrappedUnbakedModel(states.stream()
                .map(p -> Pair.of(p.getLeft().retexture(textures), p.getRight()))
                .collect(Collectors.toList()), weights);
    }

}
