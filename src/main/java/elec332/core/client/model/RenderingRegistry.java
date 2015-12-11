package elec332.core.client.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.client.IIconRegistrar;
import elec332.core.client.ITextureLoader;
import elec332.core.client.model.model.IModelAndTextureLoader;
import elec332.core.client.model.model.IModelLoader;
import elec332.core.client.model.template.ElecTemplateBakery;
import elec332.core.client.render.ISpecialBlockRenderer;
import elec332.core.client.render.ISpecialItemRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
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

    public void registerModelLoader(IModelLoader modelLoader){
        this.modelLoaders.add(modelLoader);
    }

    public void registerTextureLoader(ITextureLoader textureLoader){
        this.textureLoaders.add(textureLoader);
    }

    public void registerModelTextureLoader(IModelAndTextureLoader loader){
        registerModelLoader(loader);
        registerTextureLoader(loader);
    }

    public void registerRenderer(Block block, ISpecialBlockRenderer renderer){
        if (block.getRenderType() != SPECIAL_BLOCK_RENDERER_ID)
            System.out.println("Detected useless registering of special blockrenderer, block "+"todo"+" is using the wrong renderer ID: "+block.getRenderType()+", expected "+SPECIAL_BLOCK_RENDERER_ID);
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

    protected void removeJsonErrors(ModelLoader modelLoader){
        try {
            Field field = ModelLoader.class.getDeclaredField("missingVariants");
            field.setAccessible(true);
            int i = field.getModifiers();
            Field modifier = field.getClass().getDeclaredField("modifiers");
            i &= -17;
            modifier.setAccessible(true);
            modifier.setInt(field, i);
            @SuppressWarnings("unchecked")
            Set<ModelResourceLocation> set = (Set<ModelResourceLocation>) field.get(modelLoader);
            for (Block block : Util.getBlockIterator()){
                if (block instanceof INoJsonBlock) {
                    ResourceLocation s = (ResourceLocation) GameData.getBlockRegistry().getNameForObject(block);
                    set.remove(new ModelResourceLocation(s, "normal"));
                }
            }
            for (Item item : Util.getItemIterator()){
                if (item instanceof INoJsonItem){
                    ResourceLocation s = (ResourceLocation) GameData.getItemRegistry().getNameForObject(item);
                    set.remove(new ModelResourceLocation(s, "inventory"));
                } else if (item instanceof ItemBlock){
                    ResourceLocation s = (ResourceLocation) GameData.getBlockRegistry().getNameForObject(((ItemBlock) item).getBlock());
                    set.remove(new ModelResourceLocation(s, "inventory"));
                }
            }
        } catch (Exception e1){
            e1.printStackTrace();
        }
    }

    private class IconRegistrar implements IIconRegistrar {

        public IconRegistrar(TextureStitchEvent event){
            this.textureMap = event.map;
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
        instance.registerModelTextureLoader(new IModelAndTextureLoader() {
            @Override
            public void registerModels(ElecQuadBakery quadBakery, ElecModelBakery modelBakery, ElecTemplateBakery templateBakery) {
                for (Item item : Util.getItemIterator()){
                    if (item instanceof IModelLoader){
                        ((IModelLoader) item).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
                for (Block block : Util.getBlockIterator()){
                    if (block instanceof IModelLoader){
                        ((IModelLoader) block).registerModels(quadBakery, modelBakery, templateBakery);
                    }
                }
            }

            @Override
            public void registerTextures(IIconRegistrar iconRegistrar) {
                for (Item item : Util.getItemIterator()){
                    if (item instanceof ITextureLoader){
                        ((ITextureLoader) item).registerTextures(iconRegistrar);
                    }
                }
                for (Block block : Util.getBlockIterator()){
                    if (block instanceof ITextureLoader){
                        ((ITextureLoader) block).registerTextures(iconRegistrar);
                    }
                }
            }
        });
    }

}
