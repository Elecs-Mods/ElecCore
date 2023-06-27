package elec332.core.loader.client;

import elec332.core.api.client.model.IQuadBakery;
import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.client.RenderHelper;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Elec332 on 21-11-2015.
 */
@OnlyIn(Dist.CLIENT)
class ModelLoadEventImpl extends ModelLoadEvent {

    ModelLoadEventImpl(IQuadBakery quadBakery, Map<ResourceLocation, IBakedModel> registry, Function<ModelResourceLocation, IBakedModel> modelGetter, ModelLoader modelLoader) {
        this.quadBakery = quadBakery;
        this.registry = registry;
        this.modelGetter = modelGetter;
        this.modelLoader = modelLoader;
    }

    private final IQuadBakery quadBakery;
    private final Map<ResourceLocation, IBakedModel> registry;
    private final Function<ModelResourceLocation, IBakedModel> modelGetter;
    private final ModelLoader modelLoader;

    @Override
    @Nonnull
    public IQuadBakery getQuadBakery() {
        return quadBakery;
    }

    @Override
    public void registerModel(ResourceLocation mrl, IBakedModel model) {
        registry.put(mrl, model);
    }

    @Override
    @Nullable
    public IBakedModel getModel(ModelResourceLocation mrl) {
        return modelGetter.apply(mrl);
    }

    @Override
    @Nonnull
    public IBakedModel getMissingModel() {
        return RenderHelper.getMissingModel();
    }

    @Nonnull
    @Override
    public ModelLoader getModelLoader() {
        return modelLoader;
    }

}
