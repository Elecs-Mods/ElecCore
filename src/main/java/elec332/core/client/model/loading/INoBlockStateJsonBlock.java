package elec332.core.client.model.loading;

import com.google.common.collect.Lists;
import elec332.core.util.BlockStateHelper;
import elec332.core.util.DirectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 20-3-2017.
 */
public interface INoBlockStateJsonBlock extends IBlockModelItemLink {

    @SideOnly(Side.CLIENT)
    public VariantList getVariantsFor(IBlockState state);

    public interface RotationImpl extends INoBlockStateJsonBlock {

        @SideOnly(Side.CLIENT)
        default VariantList getVariantsFor(IBlockState state){
            Block b = state.getBlock();
            ModelRotation mr = DirectionHelper.getRotationFromFacing(state.getValue(BlockStateHelper.FACING_NORMAL.getProperty()));
            Variant variant = new Variant(b.getRegistryName(), mr, false, 1);
            return new VariantList(Lists.newArrayList(variant));
        }

    }

    public interface DefaultImpl extends INoBlockStateJsonBlock {

        @Override
        default VariantList getVariantsFor(IBlockState state){
            return new VariantList(Lists.newArrayList(new Variant(state.getBlock().getRegistryName(), ModelRotation.X0_Y0, false, 0)));
        }

    }

}
