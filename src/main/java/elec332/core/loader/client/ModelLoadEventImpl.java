package elec332.core.loader.client;

import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.api.client.model.ModelLoadEvent;
import elec332.core.client.RenderHelper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
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

    ModelLoadEventImpl(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery, Map<ResourceLocation, IBakedModel> registry, Function<ModelResourceLocation, IBakedModel> modelGetter, ModelLoader modelLoader) {
        this.quadBakery = quadBakery;
        this.modelBakery = modelBakery;
        this.templateBakery = templateBakery;
        this.registry = registry;
        this.modelGetter = modelGetter;
        this.modelLoader = modelLoader;
    }

    private final IElecQuadBakery quadBakery;
    private final IElecModelBakery modelBakery;
    private final IElecTemplateBakery templateBakery;
    private final Map<ResourceLocation, IBakedModel> registry;
    private final Function<ModelResourceLocation, IBakedModel> modelGetter;
    private final ModelLoader modelLoader;

    @Override
    @Nonnull
    public IElecQuadBakery getQuadBakery() {
        return quadBakery;
    }

    @Override
    @Nonnull
    public IElecModelBakery getModelBakery() {
        return modelBakery;
    }

    @Override
    @Nonnull
    public IElecTemplateBakery getTemplateBakery() {
        return templateBakery;
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
