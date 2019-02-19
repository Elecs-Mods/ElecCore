package elec332.core.client.model.loading;

import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import elec332.core.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Created by Elec332 on 20-3-2017.
 */
public interface INoBlockStateJsonBlock extends IBlockModelItemLink {

    @OnlyIn(Dist.CLIENT)
    public VariantList getVariantsFor(IBlockState state);

    default public boolean hasTextureOverrideJson(IBlockState state) {
        return true;
    }

    default public ResourceLocation getTextureOverridesJson(IBlockState state, Variant variant) {
        return new ResourceLocation(variant.getModelLocation().toString() + "_overrides");
    }

    public default void addAdditionalData(IBlockState state, Map<String, String> dataMap) {
    }

    public interface RotationImpl extends INoBlockStateJsonBlock {

        @OnlyIn(Dist.CLIENT)
        default VariantList getVariantsFor(IBlockState state) {
            Block b = state.getBlock();
            ModelRotation mr = RenderHelper.getDefaultRotationFromFacing(state.get(BlockProperties.FACING_NORMAL));
            Variant variant = new Variant(b.getRegistryName(), mr, false, 1);
            return new VariantList(Lists.newArrayList(variant));
        }

    }

    public interface DefaultImpl extends INoBlockStateJsonBlock {

        @Override
        default VariantList getVariantsFor(IBlockState state) {
            return new VariantList(Lists.newArrayList(new Variant(state.getBlock().getRegistryName(), ModelRotation.X0_Y0, false, 0)));
        }

    }

}
