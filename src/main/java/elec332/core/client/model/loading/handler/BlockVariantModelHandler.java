package elec332.core.client.model.loading.handler;

import com.google.common.collect.Maps;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.model.loading.INoBlockStateJsonBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.util.FMLUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.VariantList;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by Elec332 on 20-3-2017.
 */
@ModelHandler
public class BlockVariantModelHandler implements IModelHandler {

    public BlockVariantModelHandler(){
        this.blockResourceLocations = Maps.newHashMap();
        ModelLoaderRegistry.registerLoader(new ModelLoader());
    }

    private Map<ModelResourceLocation, IBlockState> blockResourceLocations;

    @Override
    public void getModelHandlers(List<?> list) {
        //
    }

    @Override
    public void registerModels() {
        ModelManager modelManager = Minecraft.getMinecraft().modelManager;
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()){
            if (block instanceof INoBlockStateJsonBlock && !(block instanceof INoJsonBlock)){
                modelManager.getBlockModelShapes().getBlockStateMapper().registerBlockStateMapper(block, new StateMapperBase() {

                    @Override
                    @Nonnull
                    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
                        ModelResourceLocation mrl = new ModelResourceLocation(new ResourceLocation("variantHandled", state.getBlock().getRegistryName().toString().replace(":", "_")), getPropertyString(state.getProperties()));
                        blockResourceLocations.put(mrl, state);
                        return mrl;
                    }

                });
            }
        }
    }

    @Override
    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter) {
        return Maps.newHashMap();
    }

    private class ModelLoader implements ICustomModelLoader {

        @Override
        public boolean accepts(ResourceLocation modelLocation) {
            return modelLocation instanceof ModelResourceLocation && blockResourceLocations.keySet().contains(modelLocation);
        }

        @Override
        public IModel loadModel(ResourceLocation modelLocation) throws Exception {
            if (!(modelLocation instanceof ModelResourceLocation)){
                throw new RuntimeException();
            }
            ModelResourceLocation fixed = new ModelResourceLocation(new ResourceLocation(modelLocation.getResourcePath().replace("_", ":")), ((ModelResourceLocation) modelLocation).getVariant());
            IBlockState state = blockResourceLocations.get(modelLocation);
            if (state == null){
                throw new IllegalStateException();
            }
            return (IModel) FMLUtil.loadClass(ModelLoader.class.getCanonicalName()+"$WeightedRandomModel").getConstructor(ResourceLocation.class, VariantList.class).newInstance(fixed, ((INoBlockStateJsonBlock) state.getBlock()).getVariantsFor(state));
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            //
        }

    }

}
