package elec332.core.client.model.replace;

import elec332.core.client.model.INoJsonItem;
import elec332.core.client.model.INoJsonBlock;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.ItemModelMesherForge;

/**
 * Created by Elec332 on 19-11-2015.
 */
@SuppressWarnings("deprecation")
public class ElecItemModelMesher extends ItemModelMesherForge {

    public ElecItemModelMesher(ModelManager manager) {
        super(manager);
    }

    @Override
    protected IBakedModel getItemModel(Item item, int meta) {
        if (item instanceof INoJsonItem){
            return ((INoJsonItem) item).getItemModel(item, meta);
        } else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock){
            return ((INoJsonBlock) ((ItemBlock) item).getBlock()).getBlockModel(item, meta);
        }
        return super.getItemModel(item, meta);
    }

}
