package elec332.core.client.model.map;

import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.Map;

/**
 * Created by Elec332 on 6-12-2015.
 */
@SideOnly(Side.CLIENT)
public class BakedModelMetaMap<M extends IBakedModel> implements IBakedModelMetaMap<M> {

    public BakedModelMetaMap(){
        map = Maps.newHashMap();
    }

    private final Map<Integer, M> map;

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
        M ret = this.map.get(meta);
        if (ret == null){
            if (meta == OreDictionary.WILDCARD_VALUE){
                ret = this.map.get(0);
            }
            if (ret == null){
                throw new IllegalArgumentException("There is no model for meta "+meta);
            }
        }
        return ret;
    }

    /**
     * Sets the model for the given metadata.
     *
     * @param model The model you want to set for the given metadata.
     * @param meta  The meta from the model.
     */
    @Override
    public void setModelForMeta(int meta, M model) {
        map.put(meta, model);
    }

}
