package elec332.core.client.model.replace;

import elec332.core.client.model.INoJsonBlock;
import elec332.core.client.model.INoJsonItem;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ItemModelMesherForge;

/**
 * Created by Elec332 on 19-11-2015.
 */
@SuppressWarnings("deprecation")
public class ElecItemModelMesher extends ItemModelMesherForge {

    public ElecItemModelMesher(ItemModelMesher modelMesher) {
        super(modelMesher.getModelManager());
        this.itemModelMesher = modelMesher;
    }

    @SuppressWarnings("all")
    private final ItemModelMesher itemModelMesher;

    @Override
    protected IBakedModel getItemModel(Item item, int meta) {
        if (item instanceof INoJsonItem){
            return ((INoJsonItem) item).getItemModel(item, meta);
        } else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock){
            return ((INoJsonBlock) ((ItemBlock) item).getBlock()).getBlockModel(item, meta);
        }
        return super.getItemModel(item, meta); //temModelMesher.getItemModel(item, meta); Impossible... :(
    }

    /* Link-through */
/*
    @Override
    public void register(Item item, ItemMeshDefinition definition) {
        itemModelMesher.register(item, definition);
    }

    @Override
    public void register(Item item, int meta, ModelResourceLocation location) {
        itemModelMesher.register(item, meta, location);
    }

    @Override
    public void rebuildCache() {
        itemModelMesher.rebuildCache();
    }

    @Override
    public IBakedModel getItemModel(ItemStack stack) {
        return itemModelMesher.getItemModel(stack);
    }

    @Override
    public ModelManager getModelManager() {
        return itemModelMesher.getModelManager();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(Item item) {
        return itemModelMesher.getParticleIcon(item);
    }

    @Override
    public TextureAtlasSprite getParticleIcon(Item item, int meta) {
        return itemModelMesher.getParticleIcon(item, meta);
    }
*/

}
