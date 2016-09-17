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
 *
 * Unused
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class ElecTileEntityItemStackRenderer extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        IBakedModel model = Minecraft.getMinecraft().renderItem.getItemModelWithOverrides(itemStackIn, null, null);

        if ( model.isBuiltInRenderer() && model instanceof TESRItemModel){
            GlStateManager.pushMatrix();
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableRescaleNormal();
            ((TESRItemModel) model).renderTesr();
            GlStateManager.popMatrix();
            return;
        }

        super.renderByItem(itemStackIn);
    }

}
