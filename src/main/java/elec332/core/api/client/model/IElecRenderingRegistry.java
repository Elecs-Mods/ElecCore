package elec332.core.api.client.model;

import elec332.core.api.client.ITextureLoader;
import elec332.core.client.model.loading.IModelAndTextureLoader;
import elec332.core.client.model.loading.IModelLoader;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecRenderingRegistry {

    public void registerLoadableModel(ModelResourceLocation mrl);

    public void registerFakeItem(Item item);

    public void registerFakeBlock(Block block);

    public void registerLoader(IModelLoader modelLoader);

    public void registerLoader(ITextureLoader textureLoader);

    public void registerLoader(IModelAndTextureLoader loader);

    public Iterable<Block> getAllValidBlocks();

    public Iterable<Item> getAllValidItems();

}
