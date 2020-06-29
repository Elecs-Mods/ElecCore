package elec332.core.loader.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITESRItem;
import elec332.core.api.client.ITextureLoader;
import elec332.core.api.client.model.*;
import elec332.core.client.RenderHelper;
import elec332.core.client.util.AbstractTileEntityItemStackRenderer;
import elec332.core.util.*;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 18-11-2015.
 */
@StaticLoad
@OnlyIn(Dist.CLIENT)
@SuppressWarnings("unused")
public final class RenderingRegistry implements IElecRenderingRegistry {

    public static RenderingRegistry instance() {
        return instance;
    }

    private static final RenderingRegistry instance;
    private static final FieldPointer<Item, Supplier<ItemStackTileEntityRenderer>> teisrField;

    @SuppressWarnings("ConstantConditions")
    private RenderingRegistry() {
        modelLoaders = Sets.newHashSet();
        textureLoaders = Sets.newHashSet();
        extraItems = Lists.newArrayList();
        extraBlocks = Lists.newArrayList();
        extraModels = Lists.newArrayList();
        extraTextures = Lists.newArrayList();
        Preconditions.checkNotNull(((FMLModContainer) FMLHelper.findMod("eleccoreloader"))).getEventBus().register(this);
        missingModel = new ObjectReference<>();
        mcDefaultStatesAdder = ModelBakery.STATE_CONTAINER_OVERRIDES::put;
        hideStates = false;

        tileTypeRef = new ObjectReference<>();
        tileTypeLink = new TileEntity(null) {

            @Override
            @SuppressWarnings("NullableProblems")
            public TileEntityType<?> getType() {
                return tileTypeRef.get();
            }

        };
    }

    private final Set<IModelLoader> modelLoaders;
    private final Set<ITextureLoader> textureLoaders;

    private final List<Item> extraItems;
    private final List<Block> extraBlocks;

    private final List<ResourceLocation> extraModels;
    private final List<ResourceLocation> extraTextures;

    private final ObjectReference<IBakedModel> missingModel;

    private final TileEntity tileTypeLink;
    private final ObjectReference<TileEntityType<?>> tileTypeRef;

    private BiConsumer<ResourceLocation, StateContainer<Block, BlockState>> mcDefaultStatesAdder;
    private boolean hideStates;
    private AtlasTexture blockTextures;

    @APIHandlerInject
    private IElecQuadBakery quadBakery = null;
    @APIHandlerInject
    private IElecModelBakery modelBakery = null;
    @APIHandlerInject
    private IElecTemplateBakery templateBakery = null;

    @Nonnull
    @Override
    public StateContainer<Block, BlockState> registerBlockStateLocation(ResourceLocation location, IProperty<?>... properties) {
        if (mcDefaultStatesAdder == null) {
            throw new IllegalStateException();
        }
        FakeBlockStateContainer fakeContainer = new FakeBlockStateContainer(properties);
        mcDefaultStatesAdder.accept(location, fakeContainer);
        fakeContainer.getValidStates().forEach(state -> ModelLoader.addSpecialModel(BlockModelShapes.getModelLocation(location, state)));
        return fakeContainer;
    }

    @Override
    public void registerModelLocation(ResourceLocation location) {
        extraModels.add(Preconditions.checkNotNull(location));
    }

    @Override
    public void registerTextureLocation(ResourceLocation location) {
        extraTextures.add(Preconditions.checkNotNull(location));
    }

    @Override
    public Item registerFakeItem(Item item) {
        extraItems.add(item);
        return item;
    }

    @Override
    public Block registerFakeBlock(Block block) {
        extraBlocks.add(block);
        return block;
    }

    @Override
    public void registerLoader(IModelLoader modelLoader) {
        registerLoader((Object) modelLoader);
    }

    @Override
    public void registerLoader(ITextureLoader textureLoader) {
        registerLoader((Object) textureLoader);
    }

    @Override
    public void registerLoader(IModelAndTextureLoader loader) {
        registerLoader((Object) loader);
    }

    @Nonnull
    @Override
    public Iterable<Block> getAllValidBlocks() {
        List<Block> list = Lists.newArrayList(RegistryHelper.getBlockRegistry());
        list.addAll(extraBlocks);
        return list;
    }

    @Nonnull
    @Override
    public Iterable<Item> getAllValidItems() {
        List<Item> list = Lists.newArrayList(RegistryHelper.getItemRegistry());
        list.addAll(extraItems);
        return list;
    }

    @Nonnull
    @Override
    public Supplier<IBakedModel> missingModelGetter() {
        return missingModel;
    }

    @Nonnull
    @Override
    public AtlasTexture getBlockTextures() {
        if (this.blockTextures == null) {
            throw new IllegalStateException("Requested block textures too early");
        }
        return blockTextures;
    }

    private void registerLoader(Object obj) {
        if (obj instanceof IModelLoader) {
            this.modelLoaders.add((IModelLoader) obj);
        }
        if (obj instanceof ITextureLoader) {
            this.textureLoaders.add((ITextureLoader) obj);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onTextureStitch(TextureStitchEvent event) {
        if (!event.getMap().getTextureLocation().equals(RenderHelper.getBlocksResourceLocation())) {
            return;
        }
        blockTextures = event.getMap();
        IIconRegistrar iconRegistrar = new IconRegistrar(event);
        for (ITextureLoader loader : textureLoaders) {
            loader.registerTextures(iconRegistrar);
        }
        for (ResourceLocation location : extraTextures) {
            iconRegistrar.registerSprite(location);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onJsonModelLoad(ModelLoadEvent event) {
        for (ResourceLocation mrl : extraModels) {
            IBakedModel model;
            try {
                //IUnbakedModel model_ = ModelLoaderRegistry.getModel(mrl);
                //model = model_.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new SimpleModelTransform(model_.getDefaultState(), false), DefaultVertexFormats.ITEM);
                IUnbakedModel model_ = event.getModelLoader().getModelOrMissing(mrl);
                model = model_.bakeModel(event.getModelLoader(), ModelLoader.defaultTextureGetter(), ModelRotation.X0_Y0, mrl);
            } catch (Exception e) {
                model = RenderHelper.getMissingModel();
                ElecCore.logger.error("Exception loading blockstate for the variant {}: ", new ResourceLocation(mrl.getNamespace(), mrl.getPath()), e);
            }
            event.registerModel(mrl, model);
        }
        for (IModelLoader loader : modelLoaders) {
            loader.registerModels(event.getQuadBakery(), event.getModelBakery(), event.getTemplateBakery());
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void afterAllModelsBaked(ModelBakeEvent event) {
        removeJsonErrors(event.getModelLoader(), event.getModelManager(), event.getModelRegistry());
        for (Item item : instance().getAllValidItems()) {
            if (item instanceof ITESRItem) {
                setItemRenderer(item, new LinkedISTESR((ITESRItem) item));
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends TileEntity> TileEntityRenderer<T> getTESR(TileEntityType<T> tile) {
        tileTypeRef.set(tile);
        return (TileEntityRenderer<T>) getTESR(tileTypeLink);
    }

    @Override
    public <T extends TileEntity> TileEntityRenderer<T> getTESR(T tile) {
        return TileEntityRendererDispatcher.instance.getRenderer(tile);
    }

    @Override
    public <T extends TileEntity> void setItemRenderer(Item item, T tile) {
        setItemRenderer(item, getTESR(tile));
    }

    @Override
    public <T extends TileEntity> void setItemRenderer(Item item, TileEntityType<T> tile) {
        setItemRenderer(item, getTESR(tile));
    }

    @Override
    public void setItemRenderer(Item item, TileEntityRenderer<?> renderer) {
        Preconditions.checkNotNull(renderer);
        setItemRenderer(Preconditions.checkNotNull(item), new AbstractTileEntityItemStackRenderer() {

            @Override
            @SuppressWarnings("ConstantConditions")
            protected void renderItem(ItemStack stack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
                renderer.render(null, 0, matrixStack, renderTypeBuffer, combinedLightIn, combinedLightIn);
                //RenderHelper.renderTileEntityAt(renderer, null, 0, 0, 0, 0);
                //renderer.render(null, 0, 0, 0, 0, 0);
            }

        });
    }

    @Override
    public void setItemRenderer(Item item, final ItemStackTileEntityRenderer renderer) {
        teisrField.set(item, () -> renderer);
    }

    @APIHandlerInject
    public void injectRenderingRegistry(IAPIHandler apiHandler) {
        apiHandler.inject(instance(), IElecRenderingRegistry.class);
    }

    @SuppressWarnings("all")
    void removeJsonErrors(ModelLoader modelLoader, ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry) {
        ElecCore.logger.info("Cleaning up internal Json stuff...");
        try {
            //Set<ModelResourceLocation> set = (Set<ModelResourceLocation>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("missingVariants")).get(modelLoader);
            Map<ResourceLocation, Exception> exceptionMap = (Map<ResourceLocation, Exception>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("loadingExceptions")).get(modelLoader);
            //if (ElecCore.removeJSONErrors){
            //    exceptionMap.clear();
            //}
            for (ModelResourceLocation rl : getValidLocations(modelManager, modelRegistry, modelLoader)) {
                //set.remove(rl);
                exceptionMap.remove(rl);
            }
            ElecModelManager.INSTANCE.cleanModelLoadingExceptions(exceptionMap);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ElecCore.logger.info("Finished cleaning up internal Json stuff.");
    }

    private Set<ModelResourceLocation> getValidLocations(ModelManager modelManager, Map<ResourceLocation, IBakedModel> modelRegistry, ModelLoader modelLoader) {
        return ElecModelManager.INSTANCE.registerBakedModels(modelRegistry, modelManager::getModel, modelLoader);
    }

    private static class LinkedISTESR extends AbstractTileEntityItemStackRenderer {

        private LinkedISTESR(ITESRItem itesrItem) {
            this.itesrItem = itesrItem;
        }

        private final ITESRItem itesrItem;

        @Override
        protected void renderItem(ItemStack stack, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer renderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
            itesrItem.renderItem(stack, matrixStack, renderTypeBuffer, combinedLightIn, combinedOverlayIn);
        }

    }

    private static class IconRegistrar implements IIconRegistrar {

        private IconRegistrar(TextureStitchEvent event) {
            this.textureMap = event.getMap();
            this.register = event instanceof TextureStitchEvent.Pre ? ((TextureStitchEvent.Pre) event)::addSprite : null;
        }

        private final AtlasTexture textureMap;
        private final Consumer<ResourceLocation> register;

        @Override
        public TextureAtlasSprite registerSprite(ResourceLocation location) {
            if (register != null) {
                register.accept(location);
            }
            //textureMap.registerSprite(Minecraft.getInstance().getResourceManager(), location);
            return textureMap.getSprite(location);
        }

        @Override
        public AtlasTexture getTextureMap() {
            return this.textureMap;
        }

    }

    private class FakeBlockStateContainer extends StateContainer<Block, BlockState> {

        private FakeBlockStateContainer(IProperty<?>... properties) {
            super(Blocks.AIR, BlockState::new, Arrays.stream(properties).collect(Collectors.toMap(IProperty::getName, p -> p)));
        }

        @Nonnull
        @Override
        public ImmutableList<BlockState> getValidStates() {
            if (hideStates) {
                return ImmutableList.of();
            }
            return super.getValidStates();
        }

    }

    static {
        ModelBakery.STATE_CONTAINER_OVERRIDES = new HashMap<ResourceLocation, StateContainer<Block, BlockState>>(ModelBakery.STATE_CONTAINER_OVERRIDES) {

            @Override
            public void forEach(BiConsumer<? super ResourceLocation, ? super StateContainer<Block, BlockState>> action) {
                instance().hideStates = true;
                instance().mcDefaultStatesAdder = null;
                super.forEach(action);
                instance().hideStates = false;
            }

        };
        instance = new RenderingRegistry();
        instance.registerLoader(new IModelAndTextureLoader() {

            @Override
            public void registerModels(IElecQuadBakery quadBakery, IElecModelBakery modelBakery, IElecTemplateBakery templateBakery) {
                for (Item item : instance.getAllValidItems()) {
                    if (item instanceof IModelLoader) {
                        ((IModelLoader) item).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
                for (Block block : instance.getAllValidBlocks()) {
                    if (block instanceof IModelLoader) {
                        ((IModelLoader) block).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
            }

            @Override
            public void registerTextures(IIconRegistrar iconRegistrar) {
                for (Item item : instance.getAllValidItems()) {
                    if (item instanceof ITextureLoader) {
                        ((ITextureLoader) item).registerTextures(iconRegistrar);
                    }
                }
                for (Block block : instance.getAllValidBlocks()) {
                    if (block instanceof ITextureLoader) {
                        ((ITextureLoader) block).registerTextures(iconRegistrar);
                    }
                }
            }

        });
        Preconditions.checkNotNull(((FMLModContainer) FMLHelper.findMod("eleccoreloader"))).getEventBus().register(new Object() {

            @SubscribeEvent(priority = EventPriority.HIGH)
            @OnlyIn(Dist.CLIENT)
            public void bakeModels(ModelBakeEvent event) {
                instance().missingModel.set(event.getModelManager().getMissingModel());
                ModLoader.get().postEvent(new ModelLoadEventImpl(instance().quadBakery, instance().modelBakery, instance.templateBakery, event.getModelRegistry(), event.getModelManager()::getModel, event.getModelLoader()));
            }

        });
        teisrField = new FieldPointer<>(Item.class, "ister");
    }

}
