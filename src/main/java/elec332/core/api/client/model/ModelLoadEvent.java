package elec332.core.api.client.model;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.eventhandler.Event;

/**
 * Created by Elec332 on 29-10-2016.
 */
public abstract class ModelLoadEvent extends Event {

    public abstract IElecQuadBakery getQuadBakery();

    public abstract IElecModelBakery getModelBakery();

    public abstract IElecTemplateBakery getTemplateBakery();

    public abstract void registerModel(ModelResourceLocation mrl, IBakedModel model);

    public abstract IBakedModel getModel(ModelResourceLocation mrl);

    public abstract IBakedModel getMissingModel();

}
