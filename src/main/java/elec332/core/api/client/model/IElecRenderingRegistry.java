package elec332.core.api.client.model;

import elec332.core.api.client.ITextureLoader;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.item.Item;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecRenderingRegistry {

    @Nonnull
    StateContainer<Block, BlockState> registerBlockStateLocation(@Nonnull ResourceLocation location, IProperty<?>... properties);

    void registerModelLocation(ResourceLocation location);

    void registerTextureLocation(ResourceLocation location);

    Item registerFakeItem(Item item);

    Block registerFakeBlock(Block block);

    void registerLoader(IModelLoader modelLoader);

    void registerLoader(ITextureLoader textureLoader);

    void registerLoader(IModelAndTextureLoader loader);

    @Nonnull
    Iterable<Block> getAllValidBlocks();

    @Nonnull
    Iterable<Item> getAllValidItems();

    @Nonnull
    Supplier<IBakedModel> missingModelGetter();

    @Nonnull
    AtlasTexture getBlockTextures();

    <T extends TileEntity> TileEntityRenderer<T> getTESR(TileEntityType<T> tile);

    <T extends TileEntity> TileEntityRenderer<T> getTESR(T tile);

    <T extends TileEntity> void setItemRenderer(Item item, Class<T> tile);

    <T extends TileEntity> void setItemRenderer(Item item, T tile);

    <T extends TileEntity> void setItemRenderer(Item item, TileEntityType<T> tile);

    void setItemRenderer(Item item, TileEntityRenderer<?> renderer);

    void setItemRenderer(Item item, ItemStackTileEntityRenderer renderer);

}
