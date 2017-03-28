package elec332.core.client.model.loading;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 * Created by Elec332 on 26-3-2017.
 */
public interface INoUnlistedBlockStateJsonBlock extends INoBlockStateJsonBlock {

    default public boolean hasTextureOverrideJson(IBlockState state){
        return true;
    }

    default public ResourceLocation getTextureOverridesJson(IBlockState state, Variant variant){
        return new ResourceLocation(variant.getModelLocation().toString()+"_overrides");
    }

    public void addAdditionalData(IBlockState state, Map<String, String> dataMap);

}
