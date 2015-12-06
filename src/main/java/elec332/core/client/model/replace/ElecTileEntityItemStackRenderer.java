package elec332.core.client.model.replace;

import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.INoJsonItem;
import elec332.core.client.model.model.IItemModel;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 27-11-2015.
 */
public class ElecTileEntityItemStackRenderer extends TileEntityItemStackRenderer {

    @Override
    public void renderByItem(ItemStack itemStackIn) {
        if (itemStackIn == null || itemStackIn.getItem() == null)
            return;
        Item item = itemStackIn.getItem();
        IBakedModel model = null;
        if (item instanceof INoJsonItem){
            model = ((INoJsonItem) item).getItemModel(itemStackIn.getItem(), itemStackIn.getItemDamage());
        } else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock){
            model = ((INoJsonBlock) ((ItemBlock) item).getBlock()).getBlockModel(itemStackIn.getItem(), itemStackIn.getItemDamage());
        }
        if (model != null && model.isBuiltInRenderer()){
            model.getGeneralQuads(); //See TESRItemModel
        }

        super.renderByItem(itemStackIn);
    }

}
