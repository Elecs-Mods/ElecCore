package elec332.core.loader.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 18-11-2015.
 */
@StaticLoad
@OnlyIn(Dist.CLIENT)
public final class RenderingRegistry implements IElecRenderingRegistry {

    public static RenderingRegistry instance() {
        return instance;
    }

    private static final RenderingRegistry instance;
    private static final FieldPointer<Item, Supplier<ItemStackTileEntityRenderer>> teisrField;

    private RenderingRegistry() {
        modelLoaders = Sets.newHashSet();
        textureLoaders = Sets.newHashSet();
        extraItems = Lists.newArrayList();
        extraBlocks = Lists.newArrayList();
        extraModels = Lists.newArrayList();
        Preconditions.checkNotNull(((FMLModContainer) FMLHelper.findMod("eleccoreloader"))).getEventBus().register(this);
        missingModel = new ObjectReference<>();
    }

    private final Set<IModelLoader> modelLoaders;
    private final Set<ITextureLoader> textureLoaders;

    private final List<Item> extraItems;
    private final List<Block> extraBlocks;

    private final List<ModelResourceLocation> extraModels;

    private final ObjectReference<IBakedModel> missingModel;

    @APIHandlerInject
    private IElecQuadBakery quadBakery = null;
    @APIHandlerInject
    private IElecModelBakery modelBakery = null;
    @APIHandlerInject
    private IElecTemplateBakery templateBakery = null;

    @Override
    public void registerLoadableModel(ModelResourceLocation mrl) {
        extraModels.add(mrl);
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
        IIconRegistrar iconRegistrar = new IconRegistrar(event);
        for (ITextureLoader loader : textureLoaders) {
            loader.registerTextures(iconRegistrar);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onJsonModelLoad(ModelLoadEvent event) {
        for (ModelResourceLocation mrl : extraModels) {
            IBakedModel model;
            try {
                IModel model_ = ModelLoaderRegistry.getModel(new ResourceLocation(mrl.getNamespace(), mrl.getPath()));
                model = model_.bake(event.getModelLoader(), ModelLoader.defaultTextureGetter(), new BasicState(model_.getDefaultState(), false), DefaultVertexFormats.ITEM);
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
    public void setItemRenderer(Item item, Class<? extends TileEntity> renderer) {
        setItemRenderer(item, Preconditions.checkNotNull(TileEntityRendererDispatcher.instance.getRenderer(renderer)));
    }

    @Override
    public void setItemRenderer(Item item, TileEntityRenderer<?> renderer) {
        Preconditions.checkNotNull(renderer);
        setItemRenderer(Preconditions.checkNotNull(item), new AbstractTileEntityItemStackRenderer() {

            @Override
            protected void renderItem(ItemStack stack) {
                renderer.render(null, 0, 0, 0, 0, 0);
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
        protected void renderItem(ItemStack stack) {
            itesrItem.renderItem(stack);
        }

    }

    private static class IconRegistrar implements IIconRegistrar {

        private IconRegistrar(TextureStitchEvent event) {
            this.textureMap = event.getMap();
            this.register = event instanceof TextureStitchEvent.Pre ? ((TextureStitchEvent.Pre) event)::addSprite : null;
        }

        private final AtlasTexture textureMap;
        @Nullable
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

    static {
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
        MinecraftForge.EVENT_BUS.register(new Object() {

            @SubscribeEvent(priority = EventPriority.HIGH)
            @OnlyIn(Dist.CLIENT)
            public void bakeModels(ModelBakeEvent event) {
                instance().missingModel.set(event.getModelManager().getMissingModel());
                MinecraftForge.EVENT_BUS.post(new ModelLoadEventImpl(instance().quadBakery, instance().modelBakery, instance.templateBakery, event.getModelRegistry(), event.getModelManager()::getModel, event.getModelLoader()));
            }

        });
        teisrField = new FieldPointer<>(Item.class, "teisr");
    }

}
