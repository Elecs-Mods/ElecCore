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
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingStage;

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
        MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, this::registerModels);
    }

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
    }

    public void registerModels(ModelRegistryEvent event) {
        ElecCore.logger.info("Registering models");
        for (IModelHandler modelHandler : modelHandlers) {
            modelHandler.preHandleModels();
        }
    }

    Set<ModelResourceLocation> registerBakedModels(Map<ModelResourceLocation, IBakedModel> registry, Function<ModelResourceLocation, IBakedModel> modelGetter) {
        ElecCore.logger.info("Handling models");
        Set<ModelResourceLocation> ret = Sets.newHashSet();
        IBakedModel missingModel = Preconditions.checkNotNull(registry.get(ModelBakery.MODEL_MISSING));

        Map<ModelResourceLocation, IBakedModel> models = Maps.newHashMap();

        for (IModelHandler modelHandler : modelHandlers) {
            models.putAll(modelHandler.registerBakedModels(modelGetter));
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
