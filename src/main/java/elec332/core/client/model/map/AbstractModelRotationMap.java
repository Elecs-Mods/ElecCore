package elec332.core.client.model.map;

import elec332.core.api.client.model.map.IBakedModelRotationMap;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
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
