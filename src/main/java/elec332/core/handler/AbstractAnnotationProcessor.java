package elec332.core.handler;

import com.google.common.collect.Maps;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.main.ElecCore;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
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
public abstract class AbstractAnnotationProcessor implements IASMDataProcessor {

    public AbstractAnnotationProcessor(){
        processList = Maps.newHashMap();
        registerProcesses();
    }

    protected static final Logger logger = ElecCore.logger;
    private final Map<Class<? extends Annotation>, Consumer<ASMDataTable.ASMData>> processList;


    protected abstract void registerProcesses();

    protected void registerDataProcessor(Class<? extends Annotation> clazz, Consumer<ASMDataTable.ASMData> consumer, boolean register){
        if (register){
            registerDataProcessor(clazz, consumer);
        }
    }

    protected void registerDataProcessor(Class<? extends Annotation> clazz, Consumer<ASMDataTable.ASMData> consumer){
        if (clazz == null || consumer == null){
            throw new IllegalArgumentException();
        }
        processList.put(clazz, consumer);
    }

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        for (Map.Entry<Class<? extends Annotation>, Consumer<ASMDataTable.ASMData>> entry : processList.entrySet()){
            if (entry.getKey() != null && entry.getValue() != null){
                for (ASMDataTable.ASMData data : asmData.getAnnotationList(entry.getKey())){
                    entry.getValue().accept(data);
                }
            }
        }
    }

    @Nonnull
    @SuppressWarnings("all")
    protected Object instantiate(Class<?> clazz, Object... params){
        return instantiate(clazz, true, params);
    }

    @Nullable
    protected Object instantiate(Class<?> clazz, boolean crash, Object... params){
        try {
            Constructor c = null;
            if (params == null || params.length == 0){
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
        } catch (Exception e){
            if (crash){
                throw new RuntimeException(e);
            } else {
                logger.error("Error invocating class: "+clazz.getCanonicalName());
                logger.error(e);
                return null;
            }
        }
    }

    @Nonnull
    @SuppressWarnings("all")
    protected Class<?> loadClass(ASMDataTable.ASMData asmData){
        return loadClass(asmData, true);
    }

    @Nullable
    protected Class<?> loadClass(ASMDataTable.ASMData asmData, boolean crash){
        try {
            return Class.forName(asmData.getClassName(), true, Loader.instance().getModClassLoader());
        } catch (Exception e){
            if (crash) {
                throw new RuntimeException(e);
            } else {
                logger.error("Error loading class: "+asmData.getClassName());
                logger.error(e);
                return null;
            }
        }
    }

}