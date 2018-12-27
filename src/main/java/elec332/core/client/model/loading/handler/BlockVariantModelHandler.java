package elec332.core.client.model.loading.handler;

import com.google.common.base.Charsets;
import com.google.common.collect.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.loading.INoBlockStateJsonBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.loader.client.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 20-3-2017.
 */
@ModelHandler
public class BlockVariantModelHandler implements IModelHandler {

    public BlockVariantModelHandler() {
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
    @SuppressWarnings("all")
    public void preHandleModels() {
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()) {
            if (block instanceof INoBlockStateJsonBlock && !(block instanceof INoJsonBlock)) {

                ResourceLocation baseName = new ResourceLocation("varianthandled", block.getRegistryName().toString().replace(":", "_"));
                uniqueNames.add(baseName);

                net.minecraftforge.client.model.ModelLoader.setCustomStateMapper(block, new StateMapperBase() {

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
        for (ResourceLocation mrl : blockResourceLocations.keySet()) {
            Throwable ex = exceptionMap.remove(mrl);
            if (ex == null) {
                continue;
            }
            while ((ex = ex.getCause()) != null) {
                if (ex instanceof FileNotFoundException) {
                    ResourceLocation fixed = getFixedLocation(mrl);
                    if (one.add(mrl)) {
                        exceptionMap.put(new ModelResourceLocation(fixed, "all"), new ModelLoaderRegistry.LoaderException("Exception loading model json for " + fixed, ex));
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

    private ResourceLocation getFixedLocation(ResourceLocation rl) {
        return new ResourceLocation(rl.getResourcePath().replace("_", ":"));
    }

    private class ModelLoader implements ICustomModelLoader {

        @Override
        public boolean accepts(@Nonnull ResourceLocation modelLocation) {
            return modelLocation instanceof ModelResourceLocation && blockResourceLocations.keySet().contains(modelLocation);
        }

        @Nonnull
        @Override
        public IModel loadModel(@Nonnull ResourceLocation modelLocation) throws Exception {
            if (!(modelLocation instanceof ModelResourceLocation)) {
                throw new RuntimeException();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            IBlockState ibs = blockResourceLocations.get(modelLocation);
            if (ibs == null) {
                throw new IllegalStateException();
            }
            Block block = ibs.getBlock();
            Map<String, String> props = addNormalProperties(ibs, Maps.newHashMap());
            boolean unlProp = ((INoBlockStateJsonBlock) block).hasTextureOverrideJson(ibs);
            List<Variant> variants = ((INoBlockStateJsonBlock) block).getVariantsFor(ibs).getVariantList();
            List<Pair<Variant, TextureOverrideData>> data = variants.stream().map((Variant variant) -> {

                Pair<Variant, TextureOverrideData> nullRet = Pair.of(variant, null);
                if (!unlProp) {
                    return nullRet;
                }
                ResourceLocation location = ((INoBlockStateJsonBlock) block).getTextureOverridesJson(ibs, variant);
                if (location == null) {
                    return nullRet;
                }
                try {
                    IResource iresource = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation(location.getResourceDomain(), "models/" + location.getResourcePath() + ".json"));
                    Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
                    Pair<Variant, TextureOverrideData> ret = Pair.of(variant, gson.fromJson(reader, TextureOverrideData.class));
                    reader.close();
                    return ret;
                } catch (Exception e) {
                    if (!(e instanceof FileNotFoundException)) {
                        System.out.println("Exception while loading texture overrides: " + location);
                        e.printStackTrace();
                    }
                    return nullRet;
                }

            }).collect(Collectors.toList());
            List<ResourceLocation> locations = Lists.newArrayList();
            Set<ResourceLocation> textures = Sets.newHashSet();
            List<IModel> models = Lists.newArrayList();
            IModelState defaultState;
            ImmutableList.Builder<Pair<IModel, IModelState>> builder = ImmutableList.builder();
            for (Pair<Variant, TextureOverrideData> vtp : data) {
                Variant v = vtp.getLeft();
                TextureOverrideData t = vtp.getRight();

                ResourceLocation loc = v.getModelLocation();
                locations.add(loc);

                IModel model;
                if (loc.equals(net.minecraftforge.client.model.ModelLoader.MODEL_MISSING)) {
                    model = ModelLoaderRegistry.getMissingModel();
                } else {
                    model = ModelLoaderRegistry.getModel(loc);
                }

                model = v.process(model);
                for (ResourceLocation location : model.getDependencies()) {
                    ModelLoaderRegistry.getModelOrMissing(location);
                }
                textures.addAll(model.getTextures());
                if (t != null) {
                    textures.addAll(t.getAllTextures());
                }

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

                @Nonnull
                @Override
                public Collection<ResourceLocation> getDependencies() {
                    return ImmutableList.copyOf(locations);
                }

                @Nonnull
                @Override
                public Collection<ResourceLocation> getTextures() {
                    return ImmutableSet.copyOf(textures);
                }

                @Override
                @SuppressWarnings("all")
                public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
                    if (!Attributes.moreSpecific(format, Attributes.DEFAULT_BAKED_FORMAT)) {
                        throw new IllegalArgumentException("Can't bake vanilla weighted models to the format that doesn't fit into the default one: " + format);
                    }
                    if (variants.size() == 1) {
                        IModel model = models.get(0);
                        TextureOverrideData texOv = data.get(0).getRight();
                        if (texOv != null && texOv.containsProperty(props.keySet())) {
                            bakedTextureGetter = new TextureOverride(texOv.process(props), bakedTextureGetter);
                        }
                        return bakeModel(model, MultiModelState.getPartState(state, model, 0), format, bakedTextureGetter, texOv);
                    }
                    WeightedBakedModel.Builder builder = new WeightedBakedModel.Builder();
                    for (int i = 0; i < variants.size(); i++) {
                        IModel model = models.get(i);
                        TextureOverrideData texOv = data.get(i).getRight();
                        if (texOv != null && texOv.containsProperty(props.keySet())) {
                            bakedTextureGetter = new TextureOverride(texOv.process(props), bakedTextureGetter);
                        }
                        IBakedModel bModel = bakeModel(model, MultiModelState.getPartState(state, model, i), format, bakedTextureGetter, texOv);
                        builder.add(bModel, variants.get(i).getWeight());
                    }
                    return builder.build();
                }

                @Nonnull
                @Override
                public IModelState getDefaultState() {
                    return defaultState;
                }

            };
        }

        @Override
        public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
            //
        }

        @SuppressWarnings("all")
        private IBakedModel bakeModel(IModel model, IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter, @Nullable TextureOverrideData tovd) {

            IBakedModel base = model.bake(state, format, bakedTextureGetter);
            if (tovd == null) {
                return base;
            }
            return new IBakedModel() {

                Map<IBlockState, IBakedModel> cache = new WeakHashMap<>();

                @Override
                @Nonnull
                public List<BakedQuad> getQuads(@Nullable IBlockState blockState, @Nullable EnumFacing side, long rand) {
                    if (blockState == null) {
                        return base.getQuads(null, side, rand);
                    }
                    if (cache.containsKey(blockState)) {
                        return cache.get(blockState).getQuads(blockState, side, rand);
                    }
                    Map<String, String> data = Maps.newHashMap();
                    ((INoBlockStateJsonBlock) blockState.getBlock()).addAdditionalData(blockState, data);
                    if (data.isEmpty()) {
                        cache.put(blockState, base);
                    }
                    IBakedModel ret;
                    cache.put(blockState, ret = model.bake(state, format, new TextureOverride(tovd.process(data), bakedTextureGetter)));
                    return ret.getQuads(blockState, side, rand);
                }

                @Override
                public boolean isAmbientOcclusion() {
                    return base.isAmbientOcclusion();
                }

                @Override
                public boolean isGui3d() {
                    return base.isGui3d();
                }

                @Override
                public boolean isBuiltInRenderer() {
                    return base.isBuiltInRenderer();
                }

                @Override
                @Nonnull
                public TextureAtlasSprite getParticleTexture() {
                    return base.getParticleTexture();
                }

                @Override
                @Nonnull
                @SuppressWarnings("deprecation")
                public ItemCameraTransforms getItemCameraTransforms() {
                    return base.getItemCameraTransforms();
                }

                @Override
                @Nonnull
                public ItemOverrideList getOverrides() {
                    return base.getOverrides();
                }

            };

        }

    }

    @SuppressWarnings("all")
    private Map<String, String> addNormalProperties(IBlockState state, Map<String, String> data) {
        for (IProperty prop : state.getPropertyKeys()) {
            data.put(prop.getName(), prop.getName(state.getValue(prop)));
        }
        return data;
    }

    private class TextureOverride implements Function<ResourceLocation, TextureAtlasSprite> {

        private TextureOverride(Map<ResourceLocation, ResourceLocation> data, Function<ResourceLocation, TextureAtlasSprite> defaultFunc) {
            this.data = data;
            this.defaultFunc = defaultFunc;
        }

        private final Map<ResourceLocation, ResourceLocation> data;
        private final Function<ResourceLocation, TextureAtlasSprite> defaultFunc;

        @Nullable
        @Override
        public TextureAtlasSprite apply(@Nullable ResourceLocation input) {
            ResourceLocation actual = data.getOrDefault(input, input);
            return defaultFunc.apply(actual);
        }

    }

    private class TextureOverrideData implements Serializable {

        @SuppressWarnings("all")
        private Map<String, Map<String, String>> textureOverrides;
        private transient Map<String, Map<String, Pair<ResourceLocation, ResourceLocation>>> processedTextureOverrides;
        private transient Set<ResourceLocation> allTextures;

        private HashMap<ResourceLocation, ResourceLocation> process(Map<String, String> data) {
            if (processedTextureOverrides == null) {
                process();
            }
            HashMap<ResourceLocation, ResourceLocation> ret = Maps.newHashMap();
            for (String s : data.keySet()) {
                if (textureOverrides.containsKey(s)) {
                    Map<String, Pair<ResourceLocation, ResourceLocation>> d = processedTextureOverrides.get(s);
                    if (d == null) {
                        continue;
                    }
                    Pair<ResourceLocation, ResourceLocation> overrde = d.get(data.get(s));
                    if (overrde == null) {
                        continue;
                    }
                    ret.put(overrde.getLeft(), overrde.getRight());
                }
            }
            return ret;
        }

        private void process() {
            processedTextureOverrides = Maps.newHashMap();
            allTextures = Sets.newHashSet();
            for (String prop : textureOverrides.keySet()) {
                Map<String, String> vl = textureOverrides.get(prop);
                if (vl == null || vl.isEmpty()) {
                    continue;
                }
                for (String value : vl.keySet()) {
                    String override = vl.get(value);
                    String[] data = override.split("-");
                    if (data.length != 2) {
                        throw new RuntimeException(textureOverrides.toString());
                    }
                    Map<String, Pair<ResourceLocation, ResourceLocation>> or = processedTextureOverrides.get(prop);
                    if (or == null) {
                        processedTextureOverrides.put(prop, or = Maps.newHashMap());
                    }
                    ResourceLocation r1 = new ResourceLocation(data[0]), r2 = new ResourceLocation(data[1]);
                    or.put(value, Pair.of(r1, r2));
                    allTextures.add(r1);
                    allTextures.add(r2);
                }
            }
        }

        private Set<ResourceLocation> getAllTextures() {
            if (allTextures == null) {
                process();
            }
            return allTextures;
        }

        private boolean containsProperty(Collection<String> props) {
            for (String s : props) {
                if (textureOverrides.containsKey(s)) {
                    return true;
                }
            }
            return false;
        }

    }

}
