package elec332.core.api.client.model.loading;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
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

    default public void cleanExceptions(Map<ResourceLocation, Exception> loaderExceptions){
    }

    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter);

}
