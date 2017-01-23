package elec332.core.client.model.replace;

import elec332.core.client.model.model.TESRItemModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by Elec332 on 27-11-2015.
 */
@SideOnly(Side.CLIENT)
public class ElecTileEntityItemStackRenderer extends TileEntityItemStackRenderer {

    public ElecTileEntityItemStackRenderer(TileEntityItemStackRenderer old){
        this.oldRenderer = old;
    }

    private final TileEntityItemStackRenderer oldRenderer;

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        IBakedModel model = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(itemStackIn, null, null);

        if (model.isBuiltInRenderer() && model instanceof TESRItemModel){
            GlStateManager.pushMatrix();
            ((TESRItemModel) model).renderTesr();
            GlStateManager.popMatrix();
            return;
        }

        oldRenderer.renderByItem(itemStackIn);
    }

}
