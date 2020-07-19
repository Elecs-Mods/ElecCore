package elec332.core.loader.client;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.client.ClientHelper;
import elec332.core.client.util.InternalResourcePack;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.ModLoadingStage;

import javax.annotation.Nonnull;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * Created by Elec332 on 11-3-2016.
 */
@StaticLoad
@OnlyIn(Dist.CLIENT)
@AnnotationDataProcessor(ModLoadingStage.COMMON_SETUP)
enum ElecModelManager implements IAnnotationDataProcessor {

    INSTANCE;

    ElecModelManager() {
        this.modelHandlers = Lists.newArrayList();
    }

    private static final String FAKE_BLOCKSTATE_JSON = "{ \"variants\": { \"\": { \"model\": \"builtin/missing\" } } }";
    private static final String MISSING_JSON = "{ \"parent\": \"builtin/generated\" }";
    private List<IModelHandler> modelHandlers;

    @Override
    public void processASMData(IAnnotationDataHandler asmData, ModLoadingStage state) {
        List<Object> list = Lists.newArrayList();
        for (IAnnotationData data : asmData.getAnnotationList(ModelHandler.class)) {
            String s = data.getClassName();
            try {
                list.add(Class.forName(s).newInstance());
            } catch (Exception e) {
                throw new RuntimeException("Error registering ModelHandler class: " + s, e);
            }
        }
        List<?> param = ImmutableList.copyOf(list);
        for (Object o : list) {
            if (o instanceof IModelHandler) {
                IModelHandler modelHandler = (IModelHandler) o;
                if (modelHandler.enabled()) {
                    modelHandlers.add(modelHandler);
                    modelHandler.getModelHandlers(param);
                }
            }
        }
        preHandleModels();
        registerFakeResources();
    }

    public void preHandleModels() {
        ElecCore.logger.info("Registering models");
        for (IModelHandler modelHandler : modelHandlers) {
            modelHandler.preHandleModels();
        }
    }

    private void registerFakeResources() {
        Map<String, Set<ResourceLocation>> mods = Maps.newHashMap();
        modelHandlers.stream()
                .map(IModelHandler::getHandlerModelLocations)
                .flatMap(Collection::stream)
                .forEach(rl -> mods.computeIfAbsent(rl.getNamespace(), k -> Sets.newHashSet()).add(rl));
        mods.keySet().forEach(name -> ClientHelper.addResourcePack(new InternalResourcePack(name + " automodel", name) {

            Set<ResourceLocation> objects = mods.get(name);

            @Nonnull
            @Override
            public InputStream getResourceStream(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location) {
                if (!location.getNamespace().equals(name)) {
                    throw new IllegalArgumentException();
                }
                String path = location.getPath();
                if (isValidBlockState(path)) {
                    return new ByteArrayInputStream(FAKE_BLOCKSTATE_JSON.getBytes());
                } else if (isValidItemModel(path)) {
                    return new ByteArrayInputStream(ModelBakery.MISSING_MODEL_MESH.getBytes());
                }
                throw new IllegalArgumentException();
            }

            @Override
            public boolean resourceExists(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location) {
                String path = location.getPath();
                return isValidBlockState(path) || isValidItemModel(path);
            }

            private boolean isValidBlockState(String path) {
                if (path.startsWith("blockstates/") && path.endsWith(".json")) {
                    String p2 = path.replace("blockstates/", "").replace(".json", "");
                    return objects.stream().anyMatch(rl -> rl.getPath().replace("block/", "").equals(p2));
                }
                return false;
            }

            private boolean isValidItemModel(String path) {
                if (path.startsWith("models/item/") && path.endsWith(".json")) {
                    String p2 = path.replace("models/item/", "")
                            .replace(".json", "");
                    return objects.stream().anyMatch(rl -> rl.getPath().replace("item/", "").equals(p2));
                }
                return false;
            }

        }));
    }

    Set<ModelResourceLocation> registerBakedModels(Map<ResourceLocation, IBakedModel> registry, Function<ModelResourceLocation, IBakedModel> modelGetter, ModelLoader modelLoader) {
        ElecCore.logger.info("Handling models");
        Set<ModelResourceLocation> ret = Sets.newHashSet();
        IBakedModel missingModel = Preconditions.checkNotNull(registry.get(ModelBakery.MODEL_MISSING));

        Map<ModelResourceLocation, IBakedModel> models = Maps.newHashMap();

        for (IModelHandler modelHandler : modelHandlers) {
            modelHandler.registerBakedModels(modelGetter, modelLoader, models::put);
        }

        for (Map.Entry<ModelResourceLocation, IBakedModel> entry : models.entrySet()) {
            registry.put(entry.getKey(), MoreObjects.firstNonNull(entry.getValue(), missingModel));
            ret.add(entry.getKey());
        }

        return ret;
    }

    void cleanModelLoadingExceptions(Map<ResourceLocation, Exception> locationExceptions) {
        for (IModelHandler modelHandler : modelHandlers) {
            modelHandler.cleanExceptions(locationExceptions);
        }
    }

}
