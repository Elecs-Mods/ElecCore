package elec332.core.client.model.map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelRotation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public class BakedModelMetaRotationMap<M extends IBakedModel> extends AbstractModelRotationMap<M> implements IBakedModelMetaRotationMap<M> {

    public BakedModelMetaRotationMap(){
        this(false, true);
    }

    public BakedModelMetaRotationMap(boolean x, boolean y) {
        super(x, y);
        this.map = Maps.newHashMap();
    }

    private final Map<Integer, Map<ModelRotation, M>> map;

    /**
     * Returns a model for metadata 0.
     *
     * @return The model for meta 0.
     */
    @Override
    public M get() {
        return forMeta(0);
    }

    /**
     * Returns a model for the specified metadata.
     *
     * @param meta The metadata for which you want to get the model.
     * @return The model for the specified meta.
     */
    @Override
    public M forMeta(int meta) {
        return forMetaAndRotation(meta, ModelRotation.X0_Y0);
    }

    /**
     * Sets the model for the given metadata.
     *
     * @param meta  The meta from the model.
     * @param model The model you want to set for the given metadata.
     */
    @Override
    public void setModelForMeta(int meta, M model) {
        setModelForMetaAndRotation(meta, ModelRotation.X0_Y0, model);
    }

    /**
     * Returns a model for the specified rotation with metadata 0.
     *
     * @param rotation The rotation you want to get a model for.
     * @return The model.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public M forRotation(ModelRotation rotation) throws RotationNotSupportedException {
        return forMetaAndRotation(0, rotation);
    }

    /**
     * Sets a model for the given rotation.
     *
     * @param rotation The rotation of the model.
     * @param model    The model for the rotation
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public void setModel(ModelRotation rotation, M model) throws RotationNotSupportedException {
        setModelForMetaAndRotation(0, rotation, model);
    }

    /**
     * Returns a model for the specified rotation and metadata.
     *
     * @param meta     The metadata from the model.
     * @param rotation The rotation of the model.
     * @return The model for the given parameters.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public M forMetaAndRotation(int meta, ModelRotation rotation) throws RotationNotSupportedException {
        Map<ModelRotation, M> map = this.map.get(meta);
        if (map == null){
            if (meta == OreDictionary.WILDCARD_VALUE){
                map = this.map.get(0);
            }
            if (map == null){
                throw new IllegalArgumentException("There is no model for meta "+meta);
            }
        }
        checkRotation(rotation);
        return map.get(rotation);
    }

    /**
     * Sets the model for the specified rotation and metadata values.
     *
     * @param meta     The metadata from the model.
     * @param rotation The rotation from the model.
     * @param model    The model you want to set for the given rotation and metadata.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public void setModelForMetaAndRotation(int meta, ModelRotation rotation, M model) throws RotationNotSupportedException {
        checkRotation(rotation);
        Map<ModelRotation, M> models = map.get(meta);
        if (models == null){
            models = Maps.newEnumMap(ModelRotation.class);
            map.put(meta, models);
        }
        models.put(rotation, model);
    }

    /**
     * Bulk version of #setModelForMetaAndRotation
     *
     * @param meta     The meta from the specified IBakedModelRotationMap
     * @param modelMap The rotation map for the specified meta.
     * @throws RotationNotSupportedException If the specified rotation is not supported.
     */
    @Override
    public void setModelsForRotation(int meta, IBakedModelRotationMap<M> modelMap) throws RotationNotSupportedException {
        List<ModelRotation> list = Lists.newArrayList();
        for (ModelRotation rotation : ModelRotation.values()){
            if (modelMap.isRotationSupported(rotation)) {
                if (!isRotationSupported(rotation))
                    throw new IllegalArgumentException();
                list.add(rotation);
            } else if (isRotationSupported(rotation)){
                throw new IllegalArgumentException();
            }
        }
        Map<ModelRotation, M> modelRotationMap = Maps.newEnumMap(ModelRotation.class);
        for (ModelRotation rotation : list){
            modelRotationMap.put(rotation, modelMap.forRotation(rotation));
        }
        map.put(meta, modelRotationMap);
    }

}
