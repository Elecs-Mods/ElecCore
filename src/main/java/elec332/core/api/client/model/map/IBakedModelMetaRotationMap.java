package elec332.core.api.client.model.map;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public interface IBakedModelMetaRotationMap<M extends IBakedModel> extends IBakedModelRotationMap<M>, IBakedModelMetaMap<M> {

    /**
     * Returns a model for the specified rotation and metadata.
     *
     * @param meta     The metadata from the model.
     * @param rotation The rotation of the model.
     * @return The model for the given parameters.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    public M forMetaAndRotation(int meta, ModelRotation rotation) throws RotationNotSupportedException;

    /**
     * Sets the model for the specified rotation and metadata values.
     *
     * @param meta     The metadata from the model.
     * @param rotation The rotation from the model.
     * @param model    The model you want to set for the given rotation and metadata.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    public void setModelForMetaAndRotation(int meta, ModelRotation rotation, M model) throws RotationNotSupportedException;

    /**
     * Bulk version of #setModelForMetaAndRotation
     *
     * @param meta     The meta from the specified IBakedModelRotationMap
     * @param modelMap The rotation map for the specified meta.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    public void setModelsForRotation(int meta, IBakedModelRotationMap<M> modelMap) throws RotationNotSupportedException;


}
