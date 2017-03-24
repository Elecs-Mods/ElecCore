package elec332.core.client.model.loading.handler;

import com.google.common.base.Function;
import com.google.common.collect.*;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.RenderingRegistry;
import elec332.core.client.model.loading.INoBlockStateJsonBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.java.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created by Elec332 on 20-3-2017.
 */
@ModelHandler
public class BlockVariantModelHandler implements IModelHandler {

    public BlockVariantModelHandler(){
        this.blockResourceLocations = Maps.newHashMap();
        this.uniqueNames = Sets.newHashSet();
        ModelLoaderRegistry.registerLoader(new ModelLoader());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final Map<ModelResourceLocation, IBlockState> blockResourceLocations;
    private final Set<ResourceLocation> uniqueNames;

    @Override
    public void getModelHandlers(List<?> list) {
        //
    }

    @Override
    public void registerModels() {
        ModelManager modelManager = Minecraft.getMinecraft().modelManager;
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()){
            if (block instanceof INoBlockStateJsonBlock && !(block instanceof INoJsonBlock)){

                ResourceLocation baseName = new ResourceLocation("varianthandled", block.getRegistryName().toString().replace(":", "_"));
                uniqueNames.add(baseName);

                modelManager.getBlockModelShapes().getBlockStateMapper().registerBlockStateMapper(block, new StateMapperBase() {

                    @Override
                    @Nonnull
                    protected ModelResourceLocation getModelResourceLocation(@Nonnull IBlockState state) {
                        ModelResourceLocation mrl = new ModelResourceLocation(baseName, getPropertyString(state.getProperties()));
                        blockResourceLocations.put(mrl, state);
                        return mrl;
                    }

                });
            }
        }
    }

    @Override
    public void cleanExceptions(Map<ResourceLocation, Exception> exceptionMap) {
        Set<ResourceLocation> one = Sets.newHashSet();
        uniqueNames.forEach(exceptionMap::remove);
        for (ResourceLocation mrl : blockResourceLocations.keySet()){
            Throwable ex = exceptionMap.remove(mrl);
            if (ex == null){
                continue;
            }
            while ((ex = ex.getCause()) != null){
                if (ex instanceof FileNotFoundException){
                    ResourceLocation fixed = getFixedLocation(mrl);
                    if (one.add(mrl)){
                        exceptionMap.put(new ModelResourceLocation(fixed, "all"), new ModelLoaderRegistry.LoaderException("Exception loading model json for "+fixed, ex));
                        break;
                    }
                }
            }
        }
    }

    @Override
    @Nonnull
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(java.util.function.Function<ModelResourceLocation, IBakedModel> bakedModelGetter) {
        return ImmutableMap.of();
    }

    private ResourceLocation getFixedLocation(ResourceLocation rl){
        return new ResourceLocation(rl.getResourcePath().replace("_", ":"));
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
            //ModelResourceLocation fixed = new ModelResourceLocation(getFixedLocation(modelLocation), ((ModelResourceLocation) modelLocation).getVariant());
            IBlockState state = blockResourceLocations.get(modelLocation);
            if (state == null){
                throw new IllegalStateException();
            }
            List<Variant> variants = ((INoBlockStateJsonBlock) state.getBlock()).getVariantsFor(state).getVariantList();
            List<ResourceLocation> locations = Lists.newArrayList();
            Set<ResourceLocation> textures = Sets.newHashSet();
            List<IModel> models = Lists.newArrayList();
            IModelState defaultState;
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            for (Variant v : variants) {
                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                IModel model;
                if(loc.equals(net.minecraftforge.client.model.ModelLoader.MODEL_MISSING)) {
                    model = ModelLoaderRegistry.getMissingModel();
                } else {
                    model = ModelLoaderRegistry.getModel(loc);
                }

                model = v.process(model);
                for(ResourceLocation location : model.getDependencies())
                {
                    ModelLoaderRegistry.getModelOrMissing(location);
                }
                textures.addAll(model.getTextures());

                models.add(model);
                builder.add(Pair.of(model, v.getState()));
            }

            if (models.size() == 0) {
                IModel missing = ModelLoaderRegistry.getMissingModel();
                models.add(missing);
                builder.add(Pair.of(missing, TRSRTransformation.identity()));
            }

            defaultState = new MultiModelState(builder.build());

            return new IModel() {

                @Override
                public Collection<ResourceLocation> getDependencies() {
                    return ImmutableList.copyOf(locations);
                }

                @Override
                public Collection<ResourceLocation> getTextures() {
                    return ImmutableSet.copyOf(textures);
                }

                @Override
                @SuppressWarnings("all")
                public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
                    if(!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT)) {
                        throw new IllegalArgumentException("can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
                    }
                    if(variants.size() == 1) {
                        IModel model = models.get(0);
                        return model.bake(MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter);
                    }
                    WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
                    for(int i = 0; i < variants.size(); i++) {
                        IModel model = models.get(i);
                        builder.add(model.bake(MultiModelState.getPartState(state, model, i), format, bakedTextureGetter), variants.get(i).getWeight());
                    }
                    return builder.build();
                }

                @Override
                public IModelState getDefaultState() {
                    return defaultState;
                }

            };
            //Class<?> clazz = FMLUtil.loadClass(net.minecraftforge.client.model.ModelLoader.class.getCanonicalName()+"$WeightedRandomModel");
            //Constructor cTor = clazz.getConstructor(ResourceLocation.class, VariantList.class);
            //cTor.setAccessible(true);
            //return (IModel) cTor.newInstance(new ResourceLocation(fixed.getResourceDomain(), fixed.getResourcePath()), ((INoBlockStateJsonBlock) state.getBlock()).getVariantsFor(state));
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            //
        }

    }

}
