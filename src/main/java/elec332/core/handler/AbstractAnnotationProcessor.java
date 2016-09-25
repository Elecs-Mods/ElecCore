package elec332.core.handler;

import com.google.common.collect.Maps;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.main.ElecCore;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.logging.log4j.Logger;

import java.lang.annotation.Annotation;
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

}