package elec332.core.api.client.model;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

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

    public void registerModel(ModelResourceLocation mrl, IBakedModel model) {
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

}
