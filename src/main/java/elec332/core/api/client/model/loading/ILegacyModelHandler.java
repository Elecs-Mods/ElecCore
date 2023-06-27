package elec332.core.api.client.model.loading;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 30-8-2020
 */
public interface ILegacyModelHandler extends IModelHandler {

    /**
     * Used to register the models to the registry,
     * a getter for fetching already existing/loaded models is provided
     *
     * @param bakedModelGetter The getter for fetching already existing/loaded models
     * @param modelLoader      The model loader
     * @param registry         The model registry, models that need to be registered can be registered here
     */
    void registerBakedModels(Function<ResourceLocation, IBakedModel> bakedModelGetter, ModelLoader modelLoader, BiConsumer<ResourceLocation, IBakedModel> registry);

    /**
     * Can be used to remove exceptions from being displayed in the logs,
     * e.g. because they have been handled/solved by this handler
     *
     * @param loaderExceptions The map with all model-loading exceptions, from which exceptions can be cleared
     */
    default void cleanExceptions(Map<ResourceLocation, Exception> loaderExceptions) {
    }

    /**
     * Used to get the names of the objects that are handled by this handler.
     * This is used to remove the model-loading error from the log files and gets called after {@link IModelHandler#preHandleModels()}
     *
     * @return A {@link Collection} with the model locations of the objects handled by this model handler
     */
    @Nonnull
    Collection<ResourceLocation> getHandlerModelLocations();

}
