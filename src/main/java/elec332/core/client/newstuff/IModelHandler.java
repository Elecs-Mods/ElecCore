package elec332.core.client.newstuff;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-9-2016.
 */
public interface IModelHandler {

    default public boolean enabled(){
        return true;
    }

    public void getModelHandlers(List<?> list);

    public void registerModels();

    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter);

}
