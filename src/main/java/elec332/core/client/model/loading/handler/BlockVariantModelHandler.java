package elec332.core.client.model.loading.handler;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.client.model.ModelCache;
import elec332.core.client.model.ModelProperties;
import elec332.core.client.model.loading.INoBlockStateJsonBlock;
import elec332.core.client.model.loading.INoJsonBlock;
import elec332.core.client.util.MultiWrappedUnbakedModel;
import elec332.core.loader.client.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockModelShapes;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.ISprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResource;
import net.minecraft.state.IProperty;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraftforge.client.model.BasicState;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.registries.ForgeRegistryEntry;
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
        this.textures = Maps.newHashMap();
        RenderingRegistry.instance().registerLoader(iconRegistrar -> textures.values().forEach(d -> d.getAllTextures().forEach(iconRegistrar::registerSprite)));
    }

    private static final Map<String, String> EMPTY_MAP = Maps.newHashMap();

    private final Map<ModelResourceLocation, BlockState> blockResourceLocations;
    private final Map<ResourceLocation, TextureOverrideData> textures;
    private final Set<ResourceLocation> uniqueNames;

    @Override
    public void getModelHandlers(List<?> list) {
        //
    }

    @Override
    @SuppressWarnings("all")
    public void preHandleModels() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        for (Block block : RenderingRegistry.instance().getAllValidBlocks()) {
            if (block instanceof INoBlockStateJsonBlock && !(block instanceof INoJsonBlock)) {
                ResourceLocation baseName = new ResourceLocation("varianthandled", block.getRegistryName().toString().replace(":", "_"));
                uniqueNames.add(baseName);

                block.getStateContainer().getValidStates().forEach(ibs -> {
                    ModelResourceLocation mrl = BlockModelShapes.getModelLocation(ibs);
                    blockResourceLocations.put(mrl, ibs);
                    List<Variant> variants = ((INoBlockStateJsonBlock) block).getVariantsFor(ibs).getVariantList();
                    variants.forEach(variant -> {
                        if (((INoBlockStateJsonBlock) block).hasTextureOverrideJson(ibs)) {
                            ResourceLocation tRes = ((INoBlockStateJsonBlock) block).getTextureOverridesJson(ibs, variant);
                            try {
                                IResource iresource = Minecraft.getInstance().getResourceManager().getResource(new ResourceLocation(tRes.getNamespace(), "models/" + tRes.getPath() + ".json"));
                                Reader reader = new InputStreamReader(iresource.getInputStream(), Charsets.UTF_8);
                                TextureOverrideData tod = gson.fromJson(reader, TextureOverrideData.class);
                                textures.put(tRes, tod);
                            } catch (Exception e) {
                                if (!(e instanceof FileNotFoundException)) {
                                    System.out.println("Exception while loading texture overrides: " + tRes);
                                    e.printStackTrace();
                                }
                            }
                        }
                        ModelLoader.addSpecialModel(variant.getModelLocation());
                    });
                });
            }
        }
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> getHandlerObjectNames() {
        return blockResourceLocations.values().stream().map(BlockState::getBlock).map(ForgeRegistryEntry::getRegistryName).collect(Collectors.toSet());
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
    public Map<ModelResourceLocation, IBakedModel> registerBakedModels(Function<ModelResourceLocation, IBakedModel> bakedModelGetter, ModelLoader modelLoader) {
        Map<ModelResourceLocation, IBakedModel> ret = Maps.newHashMap();
        blockResourceLocations.forEach((mrl, ibs) -> {
            IUnbakedModel model = getUnbakedModel(ibs, modelLoader);
            IBakedModel m = Preconditions.checkNotNull(model.bake(modelLoader, ModelLoader.defaultTextureGetter(), new BasicState(model.getDefaultState(), false), DefaultVertexFormats.BLOCK));
            ret.put(mrl, m);
        });
        return ret;
    }

    private ResourceLocation getFixedLocation(ResourceLocation rl) {
        return new ResourceLocation(rl.getPath().replace("_", ":"));
    }

    private IUnbakedModel getUnbakedModel(BlockState blockState, ModelLoader modelLoader) {
        Block block = blockState.getBlock();
        Map<String, String> props = addNormalProperties(blockState, Maps.newHashMap());
        boolean unlProp = ((INoBlockStateJsonBlock) block).hasTextureOverrideJson(blockState);
        List<Variant> variants = ((INoBlockStateJsonBlock) block).getVariantsFor(blockState).getVariantList();
        List<Pair<Variant, TextureOverrideData>> data = variants.stream().map((Variant variant) -> {

            Pair<Variant, TextureOverrideData> nullRet = Pair.of(variant, null);
            if (!unlProp) {
                return nullRet;
            }
            ResourceLocation location = ((INoBlockStateJsonBlock) block).getTextureOverridesJson(blockState, variant);
            if (location == null) {
                return nullRet;
            }
            return Pair.of(variant, textures.get(location));

        }).collect(Collectors.toList());

        return new MultiWrappedUnbakedModel(modelLoader, variants) {

            @Override
            protected IBakedModel bakeModel(IUnbakedModel model, int index, @Nonnull ModelBakery bakery, @Nonnull final Function<ResourceLocation, TextureAtlasSprite> spriteGetter, @Nonnull ISprite sprite, @Nonnull VertexFormat format) {
                TextureOverrideData texOv = data.get(index).getRight();
                Function<ResourceLocation, TextureAtlasSprite> spriteGetter_ = spriteGetter;
                if (texOv != null && texOv.containsProperty(props.keySet())) {
                    spriteGetter_ = new TextureOverride(texOv.process(props), spriteGetter);
                }
                IBakedModel base = super.bakeModel(model, index, bakery, spriteGetter_, sprite, format);
                if (texOv == null) {
                    return base;
                }
                return new ModelCache<Map<String, String>>(base) {

                    @Nonnull
                    @Override
                    public IModelData getModelData(@Nonnull IEnviromentBlockReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
                        if (tileData == EmptyModelData.INSTANCE) {
                            tileData = new ModelDataMap.Builder().build();
                        }
                        tileData.setData(ModelProperties.WORLD, world);
                        tileData.setData(ModelProperties.POS, pos);
                        return tileData;
                    }

                    @Override
                    protected Map<String, String> get(BlockState state, IModelData extraData) {
                        Map<String, String> data = Maps.newHashMap();
                        ((INoBlockStateJsonBlock) blockState.getBlock()).addAdditionalData(extraData.getData(ModelProperties.WORLD), extraData.getData(ModelProperties.POS), data);
                        if (data.isEmpty()) {
                            return EMPTY_MAP;
                        }
                        return data;
                    }

                    @Override
                    protected Map<String, String> get(ItemStack stack) {
                        return EMPTY_MAP;
                    }

                    @Nonnull
                    @Override
                    @SuppressWarnings("all")
                    protected ResourceLocation getTextureLocation() {
                        return base.getParticleTexture().getName();
                    }

                    @Override
                    protected void bakeQuads(List<BakedQuad> list, Direction side, Map<String, String> key) {
                        if (key == EMPTY_MAP) {
                            list.addAll(base.getQuads(null, side, new Random(), EmptyModelData.INSTANCE));
                        } else {
                            IBakedModel ret = model.bake(bakery, new TextureOverride(texOv.process(key), spriteGetter), sprite, format);
                            list.addAll(Preconditions.checkNotNull(ret).getQuads(null, side, new Random(), EmptyModelData.INSTANCE));
                        }
                    }

                };
            }

        };
    }

    @SuppressWarnings("all")
    private Map<String, String> addNormalProperties(BlockState state, Map<String, String> data) {
        for (IProperty prop : state.getProperties()) {
            data.put(prop.getName(), prop.getName(state.get(prop)));
        }
        return data;
    }

    private static class TextureOverride implements Function<ResourceLocation, TextureAtlasSprite> {

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

    private static class TextureOverrideData implements Serializable {

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
                    Map<String, Pair<ResourceLocation, ResourceLocation>> or = processedTextureOverrides.computeIfAbsent(prop, k -> Maps.newHashMap());
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
