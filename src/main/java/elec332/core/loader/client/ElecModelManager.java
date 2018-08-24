package elec332.core.loader.client;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IASMDataProcessor;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.IRegistry;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 11-3-2016.
 */
@StaticLoad
@SideOnly(Side.CLIENT)
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
enum ElecModelManager implements IASMDataProcessor {

    INSTANCE;

    ElecModelManager(){
        this.modelHandlers = Lists.newArrayList();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private List<IModelHandler> modelHandlers;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        List<Object> list = Lists.newArrayList();
        for (ASMDataTable.ASMData data : asmData.getAnnotationList(ModelHandler.class)){
            String s = data.getClassName();
            try {
                list.add(Class.forName(s).newInstance());
            } catch (Exception e){
                throw new RuntimeException("Error registering ModelHandler class: "+s, e);
            }
        }
        List<?> param = ImmutableList.copyOf(list);
        for (Object o : list){
            if (o instanceof IModelHandler){
                IModelHandler modelHandler = (IModelHandler) o;
                if (modelHandler.enabled()) {
                    modelHandlers.add(modelHandler);
                    modelHandler.getModelHandlers(param);
                }
            }
        }
    }

    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ElecCore.logger.info("Registering models");
        for (IModelHandler modelHandler : modelHandlers){
            modelHandler.preHandleModels();
        }
    }

    Set<ModelResourceLocation> registerBakedModels(IRegistry<ModelResourceLocation, IBakedModel> registry){
        ElecCore.logger.info("Handling models");
        Set<ModelResourceLocation> ret = Sets.newHashSet();
        IBakedModel missingModel = registry.getObject(ModelBakery.MODEL_MISSING);

        Map<ModelResourceLocation, IBakedModel> models = Maps.newHashMap();

        for (IModelHandler modelHandler : modelHandlers){
            models.putAll(modelHandler.registerBakedModels(registry::getObject));
        }

        for (Map.Entry<ModelResourceLocation, IBakedModel> entry : models.entrySet()){
            registry.putObject(entry.getKey(), MoreObjects.firstNonNull(entry.getValue(), missingModel));
            ret.add(entry.getKey());
        }

        return ret;
    }

    void cleanModelLoadingExceptions(Map<ResourceLocation, Exception> locationExceptions){
        for (IModelHandler modelHandler : modelHandlers){
            modelHandler.cleanExceptions(locationExceptions);
        }
    }

}
