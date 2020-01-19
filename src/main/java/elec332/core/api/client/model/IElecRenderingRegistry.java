package elec332.core.api.client.model;

import elec332.core.api.client.ITextureLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecRenderingRegistry {

    @Nonnull
    public StateContainer<Block, BlockState> registerBlockStateLocation(ResourceLocation location, IProperty<?>... properties);

    public void registerModelLocation(ResourceLocation location);

    public void registerTextureLocation(ResourceLocation location);

    public Item registerFakeItem(Item item);

    public Block registerFakeBlock(Block block);

    public void registerLoader(IModelLoader modelLoader);

    public void registerLoader(ITextureLoader textureLoader);

    public void registerLoader(IModelAndTextureLoader loader);

    @Nonnull
    public Iterable<Block> getAllValidBlocks();

    @Nonnull
    public Iterable<Item> getAllValidItems();

    @Nonnull
    public Supplier<IBakedModel> missingModelGetter();

    public void setItemRenderer(Item item, Class<? extends TileEntity> renderer);

    public void setItemRenderer(Item item, TileEntityRenderer<?> renderer);

    public void setItemRenderer(Item item, ItemStackTileEntityRenderer renderer);

}
