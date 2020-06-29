package elec332.core.client.util;

import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.model.IModelTransform;

/**
 * Created by Elec332 on 28-6-2020
 */
public class SimpleModelTransform implements IModelTransform {

    public SimpleModelTransform(TransformationMatrix matrix, boolean uvLock) {
        this.matrix = matrix;
        this.uvLock = uvLock;
    }

    private final TransformationMatrix matrix;
    private final boolean uvLock;

    @Override
    public TransformationMatrix getRotation() {
        return this.matrix;
    }

    @Override
    public TransformationMatrix getPartTransformation(Object part) {
        return getRotation();
    }

    @Override
    public boolean isUvLock() {
        return this.uvLock;
    }

}
