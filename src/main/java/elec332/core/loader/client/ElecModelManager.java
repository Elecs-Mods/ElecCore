package elec332.core.loader.client;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.client.model.loading.IModelHandler;
import elec332.core.api.client.model.loading.IModelManager;
import elec332.core.api.client.model.loading.ModelHandler;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.util.FMLHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;

import java.util.List;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 11-3-2016.
 */
@StaticLoad
@OnlyIn(Dist.CLIENT)
@AnnotationDataProcessor({ModLoadingStage.CONSTRUCT, ModLoadingStage.COMMON_SETUP})
enum ElecModelManager implements IModelManager, IAnnotationDataProcessor {

    INSTANCE;

    ElecModelManager() {
        this.modelHandlers = Lists.newArrayList();
        this.allHandlers = Lists.newArrayList();
        Preconditions.checkNotNull(((FMLModContainer) FMLHelper.findMod("eleccoreloader"))).getEventBus()
                .addListener(EventPriority.HIGHEST, (Consumer<GatherDataEvent>) event -> collectModelHandlers());
    }

    private final List<Object> allHandlers;
    private final List<IModelHandler> modelHandlers;
    private boolean locked;

    @APIHandlerInject
    private IAnnotationDataHandler asmData;

    @Override
    public void registerModelHandler(Object object) {
        if (locked) {
            throw new IllegalStateException();
        }
        this.allHandlers.add(object);
    }

    @Override
    public void processASMData(IAnnotationDataHandler asmData, ModLoadingStage state) {
        if (state == ModLoadingStage.CONSTRUCT) {
            for (IAnnotationData data : asmData.getAnnotationList(ModelHandler.class)) {
                String s = data.getClassName();
                try {
                    registerModelHandler(Class.forName(s).newInstance());
                } catch (Exception e) {
                    throw new RuntimeException("Error registering ModelHandler class: " + s, e);
                }
            }
        } else {
            collectModelHandlers();
        }
    }

    @APIHandlerInject
    @SuppressWarnings("unused")
    private void injectModuleManager(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, IModelManager.class);
    }

    private void collectModelHandlers() {
        locked = true;
        List<?> param = ImmutableList.copyOf(allHandlers);
        allHandlers.clear();
        for (Object o : param) {
            if (o instanceof IModelHandler) {
                IModelHandler modelHandler = (IModelHandler) o;
                if (modelHandler.enabled()) {
                    modelHandlers.add(modelHandler);
                    modelHandler.collectModelHandlers(param);
                }
            }
        }
        for (IModelHandler modelHandler : modelHandlers) {
            modelHandler.preHandleModels();
        }
        for (IModelHandler modelHandler : modelHandlers) {
            if (modelHandler instanceof Runnable) {
                ((Runnable) modelHandler).run();
            }
        }
    }

}
