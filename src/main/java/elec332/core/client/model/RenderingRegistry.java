package elec332.core.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.client.IIconRegistrar;
import elec332.core.client.ITextureLoader;
import elec332.core.client.model.model.IModelAndTextureLoader;
import elec332.core.client.model.model.IModelLoader;
import elec332.core.client.model.template.ElecTemplateBakery;
import elec332.core.client.newstuff.ElecModelHandler;
import elec332.core.client.render.ISpecialBlockRenderer;
import elec332.core.client.render.ISpecialItemRenderer;
import elec332.core.java.ReflectionHelper;
import elec332.core.main.ElecCore;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 18-11-2015.
 */
@SideOnly(Side.CLIENT)
public final class RenderingRegistry {

    public static RenderingRegistry instance(){
        return instance;
    }

    private static final RenderingRegistry instance;
    private RenderingRegistry(){
        blockRendererMap = Maps.newHashMap();
        itemRendererMap = Maps.newHashMap();
        modelLoaders = Lists.newArrayList();
        textureLoaders = Lists.newArrayList();
    }

    public static final int SPECIAL_BLOCK_RENDERER_ID = 39;

    private final Map<Block, ISpecialBlockRenderer> blockRendererMap;
    private final Map<Item, ISpecialItemRenderer> itemRendererMap;
    private final List<IModelLoader> modelLoaders;
    private final List<ITextureLoader> textureLoaders;

    public void registerLoader(IModelLoader modelLoader){
        this.modelLoaders.add(modelLoader);
    }

    public void registerLoader(ITextureLoader textureLoader){
        this.textureLoaders.add(textureLoader);
    }

    public void registerLoader(IModelAndTextureLoader loader){
        registerLoader((IModelLoader)loader);
        registerLoader((ITextureLoader)loader);
    }

    public void registerRenderer(Block block, ISpecialBlockRenderer renderer){
        //if (block.getRenderType() != SPECIAL_BLOCK_RENDERER_ID)
        //    System.out.println("Detected useless registering of special blockrenderer, block "+"todo"+" is using the wrong renderer ID: "+block.getRenderType()+", expected "+SPECIAL_BLOCK_RENDERER_ID);
        if (blockRendererMap.containsKey(block)){
            System.out.println("Replacing renderer for: "+ "todo");
        }
        blockRendererMap.put(block, renderer);
    }

    public void registerRenderer(Item item, ISpecialItemRenderer renderer){
        if (itemRendererMap.containsKey(item)){
            System.out.println("Replacing renderer for: "+ "todo");
        }
        itemRendererMap.put(item, renderer);
    }

    public ISpecialBlockRenderer getRendererFor(Block block){
        return blockRendererMap.get(block);
    }

    public ISpecialItemRenderer getRendererFor(Item item){
        return itemRendererMap.get(item);
    }

    public boolean hasSpecialFirstPersonRenderer(Item item){
        return itemRendererMap.containsKey(item);
    }

    protected void invokeEvent(TextureStitchEvent event){
        IIconRegistrar iconRegistrar = new IconRegistrar(event);
        for (ITextureLoader loader : textureLoaders){
            loader.registerTextures(iconRegistrar);
        }
    }

    protected void invokeEvent(ReplaceJsonEvent event){
        for (IModelLoader loader : modelLoaders){
            loader.registerModels(event.quadBakery, event.modelBakery, event.templateBakery);
        }
    }

    /*protected void setItemBlockModels(ModelBakeEvent event){
        for (Item item : Util.getItemIterator()){
            if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonBlock){
                event.modelRegistry.putObject(new ModelResourceLocation((ResourceLocation) GameData.getBlockRegistry().getNameForObject(((ItemBlock) item).getBlock()), "inventory"), ((INoJsonBlock) ((ItemBlock) item).getBlock()).getBlockModel());
            }
        }
    }*/

    @SuppressWarnings("all")
    protected void removeJsonErrors(ModelLoader modelLoader){
        ElecCore.logger.info("Cleaning up internal Json stuff...");
        try {
            Set<ModelResourceLocation> set = (Set<ModelResourceLocation>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("missingVariants")).get(modelLoader);
            Map<ModelResourceLocation, Exception> exceptionMap = (Map<ModelResourceLocation, Exception>) ReflectionHelper.makeFinalFieldModifiable(ModelLoader.class.getDeclaredField("loadingExceptions")).get(modelLoader);
            if (ElecCore.removeJSONErrors){
                exceptionMap.clear();
            }
            Set<ModelResourceLocation> toRemove = Sets.newHashSet();
            Set<ModelResourceLocation> loop = Sets.newHashSet(set);
            loop.addAll(exceptionMap.keySet());
            for (ModelResourceLocation rl : getValidLocations(loop, modelLoader)){
                set.remove(rl);
                exceptionMap.remove(rl);
            }
        } catch (Exception e1){
            e1.printStackTrace();
        }
        ElecCore.logger.info("Finished cleaning up internal Json stuff.");
    }

    protected Set<ModelResourceLocation> getValidLocations(Collection<ModelResourceLocation> loop, ModelBakery modelLoader){
        //Set<ModelResourceLocation> toRemove = Sets.newHashSet();
        IRegistry<ModelResourceLocation, IBakedModel> registry = modelLoader.blockModelShapes.modelManager.modelRegistry;
        /*for (ModelResourceLocation rl : loop){
            String[] sa = rl.toString().split("#");
            String name = sa[0];
            if (sa[1].equals("inventory")){
                Item item = Item.getByNameOrId(name);
                if (item instanceof INoJsonItem){
                    toRemove.add(rl);
                    registry.putObject(rl, modelItemLink);
                } else if (item instanceof ItemBlock && ((ItemBlock) item).getBlock() instanceof INoJsonItem){
                    toRemove.add(rl);
                    registry.putObject(rl, modelItemBlockLink);
                }
            } else {
                Block block = Block.getBlockFromName(name);
                if (block instanceof INoJsonBlock) {
                    toRemove.add(rl);
                    registry.putObject(rl, forBlock((INoJsonBlock) block, block.getStateFromMeta(Integer.parseInt(sa[1]))));
                }
            }
        }
        return toRemove;*/
        return ElecModelHandler.registerBakedModels(registry);
    }

    /*private IBakedModel forBlock(final INoJsonBlock block, final IBlockState state){
        return new IBakedModel() {

            private final IBlockModelWithoutQuads bm = block.getBlockModel(state);
            private final IQuadProvider quadProvider = block.getQuadProvider(state);
            private final ItemOverrideList iol = new NoJsonItemOverrideList(block);

            @Override
            public List<BakedQuad> func_188616_a(IBlockState p_188616_1_, EnumFacing p_188616_2_, long p_188616_3_) {
                return quadProvider.getBakedQuads(p_188616_1_, p_188616_2_, p_188616_3_);
            }

            @Override
            public boolean isAmbientOcclusion() {
                return bm.isAmbientOcclusion();
            }

            @Override
            public boolean isGui3d() {
                return bm.isGui3d();
            }

            @Override
            public boolean func_188618_c() {
                return bm.isTESRItem();
            }

            @Override
            public TextureAtlasSprite getParticleTexture() {
                return bm.getParticleTexture();
            }

            @Override
            public ItemCameraTransforms getItemCameraTransforms() {
                return bm.getItemCameraTransforms();
            }

            @Override
            public ItemOverrideList func_188617_f() {
                return iol;
            }
        };
    }*/

    private class IconRegistrar implements IIconRegistrar {

        public IconRegistrar(TextureStitchEvent event){
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
            public void registerModels(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery) {
                for (Item item : Util.getItemIterator()) {
                    if (item instanceof IModelLoader) {
                        ((IModelLoader) item).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
                for (Block block : Util.getBlockIterator()) {
                    if (block instanceof IModelLoader) {
                        ((IModelLoader) block).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
            }

            @Override
            public void registerTextures(IIconRegistrar iconRegistrar) {
                for (Item item : Util.getItemIterator()) {
                    if (item instanceof ITextureLoader) {
                        ((ITextureLoader) item).registerTextures(iconRegistrar);
                    }
                }
                for (Block block : Util.getBlockIterator()) {
                    if (block instanceof ITextureLoader) {
                        ((ITextureLoader) block).registerTextures(iconRegistrar);
                    }
                }
            }
        });
    }

}
