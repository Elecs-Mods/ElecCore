package elec332.core.loader.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITextureLoader;
import elec332.core.api.client.model.*;
import elec332.core.client.RenderHelper;
import elec332.core.util.ObjectReference;
import elec332.core.util.ReflectionHelper;
import elec332.core.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelManager;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private RenderingRegistry() {
        modelLoaders = Sets.newHashSet();
        textureLoaders = Sets.newHashSet();
        extraItems = Lists.newArrayList();
        extraBlocks = Lists.newArrayList();
        extraModels = Lists.newArrayList();
        MinecraftForge.EVENT_BUS.register(this);
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
                model = model_.bake(ModelLoader.defaultModelGetter(), ModelLoader.defaultTextureGetter(), model_.getDefaultState(), false, DefaultVertexFormats.ITEM);
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
    }

    @APIHandlerInject
    public void injectRenderingRegistry(IAPIHandler apiHandler) {
        apiHandler.inject(instance(), IElecRenderingRegistry.class);
    }

    @SuppressWarnings("all")
    void removeJsonErrors(ModelLoader modelLoader, ModelManager modelManager, Map<ModelResourceLocation, IBakedModel> modelRegistry) {
        ElecCore.logger.info("Cleaning up internal Json stuff...");
        try {
            Set<ModelResourceLocation> set = (Set<ModelResourceLocation>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("missingVariants")).get(modelLoader);
            Map<ResourceLocation, Exception> exceptionMap = (Map<ResourceLocation, Exception>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("loadingExceptions")).get(modelLoader);
            //if (ElecCore.removeJSONErrors){
            //    exceptionMap.clear();
            //}
            for (ModelResourceLocation rl : getValidLocations(modelManager, modelRegistry)) {
                set.remove(rl);
                exceptionMap.remove(rl);
            }
            ElecModelManager.INSTANCE.cleanModelLoadingExceptions(exceptionMap);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ElecCore.logger.info("Finished cleaning up internal Json stuff.");
    }

    private Set<ModelResourceLocation> getValidLocations(ModelManager modelLoader, Map<ModelResourceLocation, IBakedModel> modelRegistry) {
        return ElecModelManager.INSTANCE.registerBakedModels(modelRegistry, modelLoader::getModel);
    }

    private class IconRegistrar implements IIconRegistrar {

        private IconRegistrar(TextureStitchEvent event) {
            this.textureMap = event.getMap();
        }

        private final AtlasTexture textureMap;

        @Override
        public TextureAtlasSprite registerSprite(ResourceLocation location) {
            textureMap.loadTexture();
            textureMap.registerSprite(Minecraft.getInstance().getResourceManager(), location);
            return textureMap.getAtlasSprite(location.toString());
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
                MinecraftForge.EVENT_BUS.post(new ModelLoadEventImpl(instance().quadBakery, instance().modelBakery, instance.templateBakery, event.getModelRegistry(), event.getModelManager()::getModel));
            }

        });
    }

}
