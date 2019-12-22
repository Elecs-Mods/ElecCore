package elec332.core.api.client.model.loading;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Elec332 on 18-9-2016.
 * <p>
 * Main model handler interface, used to handle
 * (register, make, ...) models for all sorts of things
 */
public interface IModelHandler {

    /**
     * @return Whether this model handler is enabled or not
     */
    default public boolean enabled() {
        return true;
    }

    /**
     * Can be used to fetch sub-handlers (annotated with {@link ModelHandler}
     *
     * @param list All objects annotated with {@link ModelHandler}
     */
    public void getModelHandlers(List<?> list);

    /**
     * Used to setup handler and prepare for registering the models.
     * <p>
     * Gets called after {@link IModelHandler#getModelHandlers(List)}
     */
    public void preHandleModels();

    /**
     * Used to register the models to the registry,
     * a getter for fetching already existing/loaded models is provided
     *
     * @param bakedModelGetter The getter for fetching already existing/loaded models
     * @param modelLoader      The model loader
     * @return A Non-null map with the models that need to be registered
     */
    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter, ModelLoader modelLoader);

    /**
     * Can be used to remove exceptions from being displayed in the logs,
     * e.g. because they have been handled/solved by this handler
     *
     * @param loaderExceptions The map with all model-loading exceptions, from which exceptions can be cleared
     */
    default public void cleanExceptions(Map<ResourceLocation, Exception> loaderExceptions) {
    }

}
