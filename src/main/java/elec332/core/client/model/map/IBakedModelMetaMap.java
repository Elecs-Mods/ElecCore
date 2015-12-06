package elec332.core.client.model.map;

import net.minecraft.client.resources.model.IBakedModel;

/**
 * Created by Elec332 on 6-12-2015.
 */
public interface IBakedModelMetaMap<M extends IBakedModel> {

    /**
     * Returns a model for metadata 0.
     *
     * @return The model for meta 0.
     */
    public M get();


    /**
     * Returns a model for the specified metadata.
     *
     * @param meta The metadata for which you want to get the model.
     * @return The model for the specified meta.
     */
    public M forMeta(int meta);

    /**
     * Sets the model for the given metadata.
     *
     * @param meta The meta from the model.
     * @param model The model you want to set for the given metadata.
     */
    public void setModelForMeta(int meta, M model);

}
