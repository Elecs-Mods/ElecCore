package elec332.core.client.model.replace;

import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.INoJsonItem;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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
        if (itemStackIn == null || itemStackIn.getItem() == null)
            return;
        Item item = itemStackIn.getItem();
        IBakedModel model = null;
        if (item instanceof INoJsonItem){
            model = ((INoJsonItem) item).getItemModel(itemStackIn, null, null);
        } else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock){
            model = ((INoJsonBlock) ((ItemBlock) item).getBlock()).getBlockModel(itemStackIn.getItem(), itemStackIn.getItemDamage());
        }
        if (model != null && model.isBuiltInRenderer()){
            model.getGeneralQuads(); //See TESRItemModel
            return;
        }

        super.renderByItem(itemStackIn);
    }

}
