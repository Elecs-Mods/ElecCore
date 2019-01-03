package elec332.core.handler.annotations;

import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.util.FMLHelper;
import net.minecraftforge.fml.ModLoadingStage;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 24-9-2016.
 */
public abstract class AbstractAnnotationProcessor implements IAnnotationDataProcessor {

    public AbstractAnnotationProcessor() {
        processList = Maps.newHashMap();
        registerProcesses();
    }

    protected static final Logger logger = ElecCore.logger;
    private final Map<Class<? extends Annotation>, Consumer<IAnnotationData>> processList;


    protected abstract void registerProcesses();

    protected void registerDataProcessor(Class<? extends Annotation> clazz, Consumer<IAnnotationData> consumer, boolean register) {
        if (register) {
            registerDataProcessor(clazz, consumer);
        }
    }

    protected void registerDataProcessor(Class<? extends Annotation> clazz, Consumer<IAnnotationData> consumer) {
        if (clazz == null || consumer == null) {
            throw new IllegalArgumentException();
        }
        processList.put(clazz, consumer);
    }

    @Override
    public void processASMData(IAnnotationDataHandler asmData, ModLoadingStage state) {
        for (Map.Entry<Class<? extends Annotation>, Consumer<IAnnotationData>> entry : processList.entrySet()) {
            if (entry.getKey() != null && entry.getValue() != null) {
                for (IAnnotationData data : asmData.getAnnotationList(entry.getKey())) {
                    entry.getValue().accept(data);
                }
            }
        }
    }

    @Nonnull
    @SuppressWarnings("all")
    protected Object instantiate(Class<?> clazz, Object... params) {
        return instantiate(clazz, true, params);
    }

    @Nullable
    protected Object instantiate(Class<?> clazz, boolean crash, Object... params) {
        try {
            Constructor c = null;
            if (params == null || params.length == 0) {
                c = clazz.getDeclaredConstructor();
            }
            if (c == null) {
                @SuppressWarnings("all")
                Class[] ctor = new Class[params.length];
                for (int i = 0; i < params.length; i++) {
                    ctor[i] = params[i].getClass();
                }
                c = clazz.getDeclaredConstructor(ctor);
            }
            c.setAccessible(true);
            return c.newInstance(params);
        } catch (Exception e) {
            if (crash) {
                throw new RuntimeException(e);
            } else {
                logger.error("Error invocating class: " + clazz.getCanonicalName());
                logger.error(e);
                return null;
            }
        }
    }

    @Nullable
    protected Class<?> loadClass(IAnnotationData asmData, boolean crash) {
        try {
            return FMLHelper.loadClass(asmData.getClassName());
        } catch (Exception e) {
            if (crash) {
                throw new RuntimeException(e);
            } else {
                logger.error("Error loading class: " + asmData.getClassName());
                logger.error(e);
                return null;
            }
        }
    }

}