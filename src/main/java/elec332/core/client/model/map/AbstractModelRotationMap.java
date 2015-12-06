package elec332.core.client.model.map;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;

/**
 * Created by Elec332 on 6-12-2015.
 */
public abstract class AbstractModelRotationMap<M extends IBakedModel> implements IBakedModelRotationMap<M> {

    public AbstractModelRotationMap(boolean x, boolean y){
        this.x = x;
        this.y = y;
    }

    private final boolean x, y;

    /**
     * Returns whether ihe specified rotation is supported by this map.
     *
     * @param rotation The rotation.
     * @return Whether the rotation is supported.
     */
    @Override
    public boolean isRotationSupported(ModelRotation rotation) {
        return (x || rotation.ordinal() < 4) && (y || rotation.ordinal() % 4 == 0);
    }

    protected void checkRotation(ModelRotation rotation) throws RotationNotSupportedException {
        if (!isRotationSupported(rotation))
            throw new RotationNotSupportedException(rotation);
    }

}
