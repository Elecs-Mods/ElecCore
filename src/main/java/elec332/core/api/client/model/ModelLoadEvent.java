package elec332.core.api.client.model;

import elec332.core.api.annotations.StaticLoad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 29-10-2016.
 * <p>
 * This class used to be abstract, but you cannot
 * subscribe to abstract events :(
 * <p>
 * Event that can be used to handle model-related stuff
 */
@StaticLoad
public class ModelLoadEvent extends Event {

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public IQuadBakery getQuadBakery() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public IModelBakery getModelBakery() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ITemplateBakery getTemplateBakery() {
        throw new UnsupportedOperationException();
    }

    @OnlyIn(Dist.CLIENT)
    public void registerModel(ResourceLocation mrl, IBakedModel model) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getModel(ModelResourceLocation mrl) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getMissingModel() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    @OnlyIn(Dist.CLIENT)
    public ModelLoader getModelLoader() {
        throw new UnsupportedOperationException();
    }

}
