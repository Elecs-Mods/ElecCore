package elec332.core.client.model.loading;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import elec332.core.client.RenderHelper;
import elec332.core.util.BlockProperties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.ModelRotation;
import net.minecraft.client.renderer.model.Variant;
import net.minecraft.client.renderer.model.VariantList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Map;

/**
 * Created by Elec332 on 20-3-2017.
 */
public interface INoBlockStateJsonBlock extends IBlockModelItemLink {

    @OnlyIn(Dist.CLIENT)
    VariantList getVariantsFor(BlockState state);

    default boolean hasTextureOverrideJson(BlockState state) {
        return true;
    }

    default ResourceLocation getTextureOverridesJson(BlockState state, Variant variant) {
        return new ResourceLocation(variant.getModelLocation().toString() + "_overrides");
    }

    default void addAdditionalData(ILightReader world, BlockPos pos, Map<String, String> dataMap) {
    }

    interface RotationImpl extends INoBlockStateJsonBlock {

        @OnlyIn(Dist.CLIENT)
        default VariantList getVariantsFor(BlockState state) {
            Block b = state.getBlock();
            ModelRotation mr = RenderHelper.getDefaultRotationFromFacing(state.get(BlockProperties.FACING_HORIZONTAL));
            Variant variant = new Variant(Preconditions.checkNotNull(b.getRegistryName()), mr.getRotation(), false, 1);
            return new VariantList(Lists.newArrayList(variant));
        }

    }

    interface DefaultImpl extends INoBlockStateJsonBlock {

        @Override
        default VariantList getVariantsFor(BlockState state) {
            return new VariantList(Lists.newArrayList(new Variant(Preconditions.checkNotNull(state.getBlock().getRegistryName()), ModelRotation.X0_Y0.getRotation(), false, 0)));
        }

    }

}
