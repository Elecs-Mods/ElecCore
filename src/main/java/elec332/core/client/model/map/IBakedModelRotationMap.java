package elec332.core.client.model.map;

import elec332.core.client.model.model.IQuadProvider;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public interface IBakedModelRotationMap<M extends IBakedModel> {

    /**
     * Returns whether the specified rotation is supported by this map.
     *
     * @param rotation The rotation.
     * @return Whether the rotation is supported.
     */
    public boolean isRotationSupported(ModelRotation rotation);

    /**
     * Returns a model for the specified rotation with metadata 0.
     *
     * @param rotation The rotation you want to get a model for.
     * @return The model.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    public M forRotation(ModelRotation rotation) throws RotationNotSupportedException;

    /**
     * Sets a model for the given rotation.
     *
     * @param rotation The rotation of the model.
     * @param model The model for the rotation
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    public void setModel(ModelRotation rotation, M model) throws RotationNotSupportedException;

    public class RotationNotSupportedException extends IllegalArgumentException {

        public RotationNotSupportedException(ModelRotation rotation){
            super("Model rotation for "+rotation+" is not supported.");
        }

    }

}
