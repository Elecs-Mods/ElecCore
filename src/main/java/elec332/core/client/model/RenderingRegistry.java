package elec332.core.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import elec332.core.api.client.IIconRegistrar;
import elec332.core.api.client.ITextureLoader;
import elec332.core.api.client.model.IElecModelBakery;
import elec332.core.api.client.model.IElecQuadBakery;
import elec332.core.api.client.model.IElecRenderingRegistry;
import elec332.core.api.client.model.IElecTemplateBakery;
import elec332.core.client.RenderHelper;
import elec332.core.client.model.loading.IModelAndTextureLoader;
import elec332.core.client.model.loading.IModelLoader;
import elec332.core.client.model.loading.handler.ElecModelHandler;
import elec332.core.java.ReflectionHelper;
import elec332.core.main.ElecCore;
import elec332.core.util.RegistryHelper;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 18-11-2015.
 */
@SideOnly(Side.CLIENT)
public final class RenderingRegistry implements IElecRenderingRegistry {

    public static RenderingRegistry instance(){
        return instance;
    }

    private static final RenderingRegistry instance;

    private RenderingRegistry(){
        modelLoaders = Sets.newHashSet();
        textureLoaders = Sets.newHashSet();
        extraItems = Lists.newArrayList();
        extraBlocks = Lists.newArrayList();
        extraModels = Lists.newArrayList();
    }

    private final Set<IModelLoader> modelLoaders;
    private final Set<ITextureLoader> textureLoaders;

    private final List<Item> extraItems;
    private final List<Block> extraBlocks;

    private final List<ModelResourceLocation> extraModels;

    @Override
    public void registerLoadableModel(ModelResourceLocation mrl){
        extraModels.add(mrl);
    }

    @Override
    public void registerFakeItem(Item item){
        extraItems.add(item);
    }

    @Override
    public void registerFakeBlock(Block block){
        extraBlocks.add(block);
    }

    @Override
    public void registerLoader(IModelLoader modelLoader){
        registerLoader((Object) modelLoader);
    }

    @Override
    public void registerLoader(ITextureLoader textureLoader){
        registerLoader((Object) textureLoader);
    }

    @Override
    public void registerLoader(IModelAndTextureLoader loader){
        registerLoader((Object) loader);
    }

    @Override
    public Iterable<Block> getAllValidBlocks(){
        List<Block> list = Lists.newArrayList(RegistryHelper.getBlockRegistry());
        list.addAll(extraBlocks);
        return list;
    }

    @Override
    public Iterable<Item> getAllValidItems(){
        List<Item> list = Lists.newArrayList(RegistryHelper.getItemRegistry());
        list.addAll(extraItems);
        return list;
    }

    private void registerLoader(Object obj){
        if (obj instanceof IModelLoader){
            this.modelLoaders.add((IModelLoader) obj);
        }
        if (obj instanceof ITextureLoader){
            this.textureLoaders.add((ITextureLoader) obj);
        }
    }

    void invokeEvent(TextureStitchEvent event){
        IIconRegistrar iconRegistrar = new IconRegistrar(event);
        for (ITextureLoader loader : textureLoaders){
            loader.registerTextures(iconRegistrar);
        }
    }

    void invokeEvent(ModelLoadEventImpl event){
        for (IModelLoader loader : modelLoaders){
            loader.registerModels(event.getQuadBakery(), event.getModelBakery(), event.getTemplateBakery());
        }
    }


    @SuppressWarnings("all")
    void removeJsonErrors(ModelLoader modelLoader){
        ElecCore.logger.info("Cleaning up internal Json stuff...");
        try {
            Set<ModelResourceLocation> set = (Set<ModelResourceLocation>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("missingVariants")).get(modelLoader);
            Map<ModelResourceLocation, Exception> exceptionMap = (Map<ModelResourceLocation, Exception>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("loadingExceptions")).get(modelLoader);
            if (ElecCore.removeJSONErrors){
                exceptionMap.clear();
            }
            for (ModelResourceLocation rl : getValidLocations(modelLoader)){
                set.remove(rl);
                exceptionMap.remove(rl);
            }
        } catch (Exception e1){
            e1.printStackTrace();
        }
        ElecCore.logger.info("Finished cleaning up internal Json stuff.");
    }

    private Set<ModelResourceLocation> getValidLocations(ModelBakery modelLoader){
        IRegistry<ModelResourceLocation, IBakedModel> registry = modelLoader.blockModelShapes.modelManager.modelRegistry;

        for (ModelResourceLocation mrl : extraModels){
            IBakedModel model;
            try {
                IModel model_ = ModelLoaderRegistry.getModel(mrl);
                model = model_.bake(model_.getDefaultState(), DefaultVertexFormats.ITEM, ModelLoader.defaultTextureGetter());
            } catch (Exception e){
                model = RenderHelper.getMissingModel();
            }
            registry.putObject(mrl, model);
        }

        return ElecModelHandler.registerBakedModels(registry);
    }

    private class IconRegistrar implements IIconRegistrar {

        private IconRegistrar(TextureStitchEvent event){
            this.textureMap = event.getMap();
        }

        private final TextureMap textureMap;

        @Override
        public TextureAtlasSprite registerSprite(ResourceLocation location) {
            textureMap.registerSprite(location);
            return textureMap.getAtlasSprite(location.toString());
        }

        @Override
        public TextureMap getTextureMap() {
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
    }

}
