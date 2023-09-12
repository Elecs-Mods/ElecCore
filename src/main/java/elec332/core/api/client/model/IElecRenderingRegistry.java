package elec332.core.api.client.model;

import elec332.core.api.client.ITextureLoader;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.client.resources.model.BakedModel;
//import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
//import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
//import net.minecraft.world.item.Item;
//import net.minecraft.world.level.block.state.properties.Property;
//import net.minecraft.state.StateContainer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.Property;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 29-10-2016.
 */
public interface IElecRenderingRegistry {

    @Nonnull
    public StateContainer<Block, BlockState> registerBlockStateLocation(ResourceLocation location, Property<?>... properties);

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
    public Supplier<BakedModel> missingModelGetter();

    public void setItemRenderer(Item item, Class<? extends BlockEntity> renderer);

    public void setItemRenderer(Item item, BlockEntityRenderer<?> renderer);

    public void setItemRenderer(Item item, BlockEntityWithoutLevelRenderer renderer);

}
