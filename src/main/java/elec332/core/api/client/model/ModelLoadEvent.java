package elec332.core.api.client.model;

import elec332.core.api.annotations.StaticLoad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
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
@OnlyIn(Dist.CLIENT)
public class ModelLoadEvent extends Event {

    @Nonnull
    public IElecQuadBakery getQuadBakery() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public IElecModelBakery getModelBakery() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public IElecTemplateBakery getTemplateBakery() {
        throw new UnsupportedOperationException();
    }

    public void registerModel(ResourceLocation mrl, IBakedModel model) {
        throw new UnsupportedOperationException();
    }

    @Nullable
    public IBakedModel getModel(ModelResourceLocation mrl) {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public IBakedModel getMissingModel() {
        throw new UnsupportedOperationException();
    }

    @Nonnull
    public ModelLoader getModelLoader() {
        throw new UnsupportedOperationException();
    }

}
