package elec332.core.client.render;

import elec332.core.client.ITessellator;
import elec332.core.client.RenderBlocks;
import elec332.core.client.RenderHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 22-9-2015.
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractBlockRenderer implements ISpecialBlockRenderer {

    @Override
    public final boolean renderBlock(IBlockAccess iba, IBlockState state, BlockPos blockPosIn, WorldRenderer renderer) {
        if (shouldRenderBlock(iba, state, blockPosIn)) {
            ITessellator tessellator = RenderHelper.forWorldRenderer(renderer);
            tessellator.setBrightness(state.getBlock().getMixedBrightnessForBlock(iba, blockPosIn));
            RenderBlocks renderBlocks = RenderHelper.getBlockRenderer().setTessellator(tessellator);
            net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
            tessellator.setColorRGBA_F(1, 1, 1, 1);
            RenderHelper.bindBlockTextures();
            renderBlock(iba, state, blockPosIn, renderBlocks, tessellator, renderer);
            renderBlocks.resetTessellator();
            return true;
        }
        return false;
    }

    public abstract void renderBlock(IBlockAccess iba, IBlockState state, BlockPos pos, RenderBlocks renderBlocks, ITessellator tessellator, WorldRenderer renderer);

    public abstract boolean shouldRenderBlock(IBlockAccess iba, IBlockState state, BlockPos pos);

}
