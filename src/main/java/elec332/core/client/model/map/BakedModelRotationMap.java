package elec332.core.client.model.map;

import com.google.common.collect.Maps;
import elec332.core.client.model.model.IQuadProvider;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;

import java.util.Map;

/**
 * Created by Elec332 on 6-12-2015.
 */
public class BakedModelRotationMap<M extends IBakedModel> extends AbstractModelRotationMap<M> {

    public BakedModelRotationMap(){
        this(false, true);
    }

    public BakedModelRotationMap(boolean x, boolean y){
        super(x, y);
        this.map = Maps.newEnumMap(ModelRotation.class);
    }

    private final Map<ModelRotation, M> map;

    /**
     * Returns a model for the specified rotation with metadata 0.
     *
     * @param rotation The rotation you want to get a model for.
     * @return The model.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public M forRotation(ModelRotation rotation) throws RotationNotSupportedException {
        checkRotation(rotation);
        return map.get(rotation);
    }

    /**
     * Sets a model for the given rotation.
     *
     * @param rotation The rotation of the model.
     * @param model    The model for the rotation
     * @throws elec332.core.client.model.map.IBakedModelRotationMap.RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public void setModel(ModelRotation rotation, M model) throws RotationNotSupportedException {
        checkRotation(rotation);
        map.put(rotation, model);
    }

}
