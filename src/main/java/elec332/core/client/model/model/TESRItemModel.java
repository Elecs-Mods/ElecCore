package elec332.core.client.model.model;

import elec332.core.client.RenderHelper;
import elec332.core.client.model.ElecModelBakery;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * Created by Elec332 on 27-11-2015.
 */
@SideOnly(Side.CLIENT)
public class TESRItemModel extends AbstractItemModel {

    public TESRItemModel(TileEntitySpecialRenderer tesr){
        this.tesr = tesr;
        this.tesr.setRendererDispatcher(TileEntityRendererDispatcher.instance);
    }

    private final TileEntitySpecialRenderer tesr;

    @Override
    public boolean isItemTESR() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public ResourceLocation getTextureLocation() {
        return null;
    }

    @Override
    public final List<BakedQuad> getGeneralQuads() {
        return EMPTY_LIST;
    }

    @Override
    @SuppressWarnings("deprecation")
    public ItemCameraTransforms getItemCameraTransforms() {
        return ElecModelBakery.DEFAULT_BLOCK;
    }

    @SuppressWarnings("unchecked")
    public void renderTesr(){
        RenderHelper.renderTileEntityAt(tesr, null, 0, 0, 0, -1, 0);
    }

}
