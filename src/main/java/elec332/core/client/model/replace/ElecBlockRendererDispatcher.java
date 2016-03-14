package elec332.core.client.model.replace;

import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.WeightedBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 15-11-2015.
 */
@SideOnly(Side.CLIENT)
public class ElecBlockRendererDispatcher{}/* extends BlockRendererDispatcher {

    public ElecBlockRendererDispatcher(BlockRendererDispatcher blockRendererDispatcher) {
        super(blockRendererDispatcher.blockModelShapes, blockRendererDispatcher.gameSettings);
        this.blockRendererDispatcher = blockRendererDispatcher;
    }

    private final BlockRendererDispatcher blockRendererDispatcher;

    @Override
    public boolean renderBlock(IBlockState state, BlockPos pos, IBlockAccess world, VertexBuffer renderer) {
        try {
            int i = state.getBlock().getRenderType();
            if (i == RenderingRegistry.SPECIAL_BLOCK_RENDERER_ID) {
                return RenderingRegistry.instance().getRendererFor(state.getBlock()).renderBlock(world, state, pos, renderer);
            } else if (i == 3){
                IBakedModel ibakedmodel = this.getModelFromBlockState(state, world, pos);
                return this.getBlockModelRenderer().renderModel(world, ibakedmodel, state, pos, renderer);
            }
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Tesselating block in world");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated" + (state.getBlock().getRenderType() == RenderingRegistry.SPECIAL_BLOCK_RENDERER_ID ? " - ISBHR" : ""));
            CrashReportCategory.addBlockInfo(crashreportcategory, pos, state.getBlock(), state.getBlock().getMetaFromState(state));
            throw new ReportedException(crashreport);
        }
        return this.blockRendererDispatcher.renderBlock(state, pos, world, renderer);
    }

    @SuppressWarnings("deprecation")
    public IBakedModel getModelFromBlockState(IBlockState state, IBlockAccess iba, BlockPos pos) {
        Block block = state.getBlock();
        if (iba.getWorldType() != WorldType.DEBUG_WORLD) {
            try {
                state = block.getActualState(state, iba, pos);
            } catch (Exception exception) {
                //No-one cares...
            }
        }

        IBakedModel ibakedmodel;

        if (block instanceof INoJsonBlock) {
            ibakedmodel = ((INoJsonBlock) block).getBlockModel(state, iba, pos);
        } else {
            ibakedmodel = this.blockRendererDispatcher.blockModelShapes.getModelForState(state);
        }

        if (pos != null && this.blockRendererDispatcher.gameSettings.allowBlockAlternatives && ibakedmodel instanceof WeightedBakedModel) {
            ibakedmodel = ((WeightedBakedModel)ibakedmodel).getAlternativeModel(MathHelper.getPositionRandom(pos));
        }

        if(ibakedmodel instanceof net.minecraftforge.client.model.ISmartBlockModel) {
            IBlockState extendedState = block.getExtendedState(state, iba, pos);
            ibakedmodel = ((net.minecraftforge.client.model.ISmartBlockModel)ibakedmodel).handleBlockState(extendedState);
        }

        return ibakedmodel;
    }

    /* Link-through *//*

    @Override
    public BlockModelShapes getBlockModelShapes() {
        return blockRendererDispatcher.getBlockModelShapes();
    }

    @Override
    public void renderBlockDamage(IBlockState state, BlockPos pos, TextureAtlasSprite texture, IBlockAccess blockAccess) {
        Block block = state.getBlock();
        if (block instanceof INoJsonBlock && block.getRenderType() == 3){
            IBakedModel model = ((INoJsonBlock) block).getBlockModel(block.getActualState(state, blockAccess, pos), blockAccess, pos);
            IBlockState extendedState = block.getExtendedState(state, blockAccess, pos);
            for (net.minecraft.util.EnumWorldBlockLayer layer : net.minecraft.util.EnumWorldBlockLayer.values()) {
                if (block.canRenderInLayer(layer)) {
                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(layer);
                    IBakedModel targetLayer = ((net.minecraftforge.client.model.ISmartBlockModel)model).handleBlockState(extendedState);
                    TextureAtlasSprite tas = elec332.core.client.RenderHelper.checkIcon(targetLayer.getParticleTexture());
                    IBakedModel damageModel = (new SimpleBakedModel.Builder(targetLayer, texture).setTexture(tas)).makeBakedModel();
                    this.getBlockModelRenderer().renderModel(blockAccess, damageModel, state, pos, Tessellator.getInstance().getWorldRenderer());
                }
            }
            return;
        }
        blockRendererDispatcher.renderBlockDamage(state, pos, texture, blockAccess);
    }

    @Override
    public BlockModelRenderer getBlockModelRenderer() {
        return blockRendererDispatcher.getBlockModelRenderer();
    }

    @Override
    public void renderBlockBrightness(IBlockState state, float brightness) {
        blockRendererDispatcher.renderBlockBrightness(state, brightness);
    }

    @Override
    public boolean isRenderTypeChest(Block block, int i) {
        return blockRendererDispatcher.isRenderTypeChest(block, i);
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        blockRendererDispatcher.onResourceManagerReload(resourceManager);
    }

}
        */