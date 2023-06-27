package elec332.core.api.client.model.loading;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import javax.annotation.Nullable;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 30-8-2020
 */
public interface IDataModelHandler extends IModelHandler {

    void fillDataProvider(DataProvider dataProvider);

    IBakedModel getModel(ResourceLocation modelLocation, IModelTransform modelTransform, ModelData models);

    void registerTextures(Consumer<ResourceLocation> textureRegister, BiConsumer<ResourceLocation, ResourceLocation> textureMaterialRegister);

    interface ModelData {

        BiFunction<ResourceLocation, IModelTransform, IBakedModel> modelGetter();

        Function<ResourceLocation, IBakedModel> bakedModelGetter();

        Function<ResourceLocation, TextureAtlasSprite> spriteGetter();

    }

    interface DataProvider {

        default void registerFullModel(Block block) {
            registerFullModel(block, block.getRegistryName());
        }

        default void registerFullModel(Block block, @Nullable BiConsumer<BlockState, ConfiguredModel.Builder<?>> configurator) {
            registerItemModel(block);
            registerBlockModel(block);
            registerBlockState(block, configurator);
        }

        default void registerFullModel(Block block, ResourceLocation model) {
            registerFullModel(block, model, null);
        }

        default void registerFullModel(Block block, ResourceLocation model, @Nullable BiConsumer<BlockState, ConfiguredModel.Builder<?>> configurator) {
            registerItemModel(model);
            registerBlockModel(model);
            registerBlockState(block, model, configurator);
        }

        default void registerItemModel(Item item) {
            registerItemModel(item.getRegistryName());
        }

        default void registerItemModel(Block block) {
            registerItemModel(block.getRegistryName());
        }

        void registerItemModel(ResourceLocation model);

        default void registerBlockModel(Block block) {
            registerBlockModel(block.getRegistryName());
        }

        void registerBlockModel(ResourceLocation model);

        default void registerBlockState(Block block) {
            registerBlockState(block, block.getRegistryName(), null);
        }

        default void registerBlockState(Block block, @Nullable BiConsumer<BlockState, ConfiguredModel.Builder<?>> configurator) {
            registerBlockState(block, block.getRegistryName(), configurator);
        }

        default void registerBlockState(Block block, ResourceLocation model) {
            registerBlockState(block, model, null);
        }

        default void registerBlockState(Block block, ResourceLocation model, @Nullable BiConsumer<BlockState, ConfiguredModel.Builder<?>> configurator) {
            registerBlockState(block, state -> model, configurator);
        }

        default void registerBlockState(Block block, Function<BlockState, ResourceLocation> modelGetter) {
            registerBlockState(block, modelGetter, null);
        }

        void registerBlockState(Block block, Function<BlockState, ResourceLocation> modelGetter, @Nullable BiConsumer<BlockState, ConfiguredModel.Builder<?>> configurator);

    }

}
