package elec332.core.client.model.replace;

import elec332.core.main.ElecCore;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelManager;

/**
 * Created by Elec332 on 18-1-2016.
 */
public class ElecModelManager extends ModelManager {

    public ElecModelManager(TextureMap textures) {
        super(textures);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        ElecCore.logger.info("NOTE: All errors in the grid below are not caused by ElecCore...");
        ElecCore.logger.info("--------------------------------------------------------------------------");
        ModelBakery modelbakery = new ElecModelLoader(resourceManager, this.texMap, this.modelProvider);
        this.modelRegistry = modelbakery.setupModelRegistry();
        this.defaultModel = (IBakedModel)this.modelRegistry.getObject(ModelBakery.MODEL_MISSING);
        net.minecraftforge.client.ForgeHooksClient.onModelBake(this, this.modelRegistry, modelbakery);
        this.modelProvider.reloadModels();
        ElecCore.logger.info("--------------------------------------------------------------------------");
    }

}
