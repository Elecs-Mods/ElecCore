package elec332.core.client.model;

import elec332.core.client.model.model.IModelAndTextureLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.Block;
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
        mtLoaders = Lists.newArrayList();
    }

    public static final int SPECIAL_BLOCK_RENDERER_ID = 39;

    private final Map<Block, ISpecialBlockRenderer> blockRendererMap;
    private final Map<Item, ISpecialItemRenderer> itemRendererMap;
    private final List<IModelAndTextureLoader> mtLoaders;

    public void registerModelTextureLoader(IModelAndTextureLoader loader){
        mtLoaders.add(loader);
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

    protected void invokeEvent(TextureStitchEvent.Pre event){
        for (IModelAndTextureLoader loader : mtLoaders){
            loader.registerTextures(event.map, this);
        }
    }

    protected void invokeEvent(ReplaceJsonEvent event){
        for (IModelAndTextureLoader loader : mtLoaders){
            loader.registerModels(event.quadBakery, this);
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

    static {
        instance = new RenderingRegistry();
        instance.registerModelTextureLoader(new IModelAndTextureLoader() {
            @Override
            public void registerModels(ElecQuadBakery quadBakery, RenderingRegistry renderingRegistry) {
                for (Item item : Util.getItemIterator()){
                    if (item instanceof IModelAndTextureLoader){
                        ((IModelAndTextureLoader) item).registerModels(quadBakery, renderingRegistry);
                    }
                }
                for (Block block : Util.getBlockIterator()){
                    if (block instanceof IModelAndTextureLoader){
                        ((IModelAndTextureLoader) block).registerModels(quadBakery, renderingRegistry);
                    }
                }
            }

            @Override
            public void registerTextures(TextureMap textureMap, RenderingRegistry renderingRegistry) {
                for (Item item : Util.getItemIterator()){
                    if (item instanceof IModelAndTextureLoader){
                        ((IModelAndTextureLoader) item).registerTextures(textureMap, renderingRegistry);
                    }
                }
                for (Block block : Util.getBlockIterator()){
                    if (block instanceof IModelAndTextureLoader){
                        ((IModelAndTextureLoader) block).registerTextures(textureMap, renderingRegistry);
                    }
                }
            }
        });
    }

}
