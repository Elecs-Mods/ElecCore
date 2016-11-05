package elec332.core.main;

import com.google.common.collect.*;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IASMDataProcessor;
import elec332.core.api.discovery.IAdvancedASMData;
import elec332.core.util.FMLUtil;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ModCandidate;
import org.objectweb.asm.Type;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 29-10-2016.
 */
public class ElecCoreDiscoverer {

    ElecCoreDiscoverer(){
        asmLoaderMap = Maps.newHashMap();
        validStates = ImmutableList.of(LoaderState.CONSTRUCTING, LoaderState.PREINITIALIZATION, LoaderState.INITIALIZATION, LoaderState.POSTINITIALIZATION, LoaderState.AVAILABLE);
    }

    private static final boolean useCache = false;
    private final Map<LoaderState, List<IASMDataProcessor>> asmLoaderMap;
    private final List<LoaderState> validStates;
    IASMDataHelper asmDataHelper;

    void identify(ASMDataTable dataTable){
        for (LoaderState state : validStates){
            asmLoaderMap.put(state, Lists.<IASMDataProcessor>newArrayList());
        }
        asmDataHelper = new IASMDataHelper() {

            private final Map<String, Set<IAdvancedASMData>> annotationData = Maps.newHashMap();

            @Override
            public ASMDataTable getASMDataTable() {
                return dataTable;
            }

            @Override
            public Set<ASMDataTable.ASMData> getAnnotationList(Class<? extends Annotation> annotationClass) {
                return getASMDataTable().getAll(annotationClass.getName());
            }

            @Override
            public Set<IAdvancedASMData> getAdvancedAnnotationList(Class<? extends Annotation> annotationClass) {
                String s = annotationClass.getName();
                if (!useCache){
                    return createNew(s);
                }
                Set<IAdvancedASMData> ret = annotationData.get(s);
                if (ret == null){
                    annotationData.put(s, ret = createNew(s));
                }
                return ret;
            }

            private Set<IAdvancedASMData> createNew(String s){
                Set<IAdvancedASMData> ret = Sets.newHashSet();
                for (ASMDataTable.ASMData data : getASMDataTable().getAll(s)){
                    ret.add(new AdvancedASMData(data));
                }
                return ImmutableSet.copyOf(ret);
            }

        };
        for (ASMDataTable.ASMData data : asmDataHelper.getAnnotationList(elec332.core.api.discovery.ASMDataProcessor.class)) {
            Map<IASMDataProcessor, LoaderState[]> dataMap = Maps.newHashMap();
            boolean eb = false;
            Class<?> clazz;
            try {
                clazz = Class.forName(data.getClassName());
            } catch (ClassNotFoundException e) {
                //Do nothing, class is probably annotated with @SideOnly
                continue;
            }
            if (clazz == null) {
                continue;
            }
            if (clazz.isAnnotationPresent(elec332.core.api.discovery.ASMDataProcessor.class)) {
                LoaderState[] ls = clazz.getAnnotation(elec332.core.api.discovery.ASMDataProcessor.class).value();
                if (clazz.isEnum()) {
                    for (Object e : clazz.getEnumConstants()) {
                        if (e instanceof IASMDataProcessor) {
                            dataMap.put((IASMDataProcessor) e, ls);
                        }
                    }
                    eb = true;
                } else {
                    Object o;
                    try {
                        o = clazz.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Error invocating annotated IASMData class: " + data.getClassName());
                    }
                    if (o instanceof IASMDataProcessor) {
                        dataMap.put((IASMDataProcessor) o, ls);
                    }
                }
            }

            if (!eb) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(elec332.core.api.discovery.ASMDataProcessor.class)) {
                        Object obj;
                        try {
                            obj = field.get(null);
                        } catch (Exception e) {
                            continue; //Not static
                        }
                        if (obj instanceof IASMDataProcessor) {
                            dataMap.put((IASMDataProcessor) obj, field.getAnnotation(elec332.core.api.discovery.ASMDataProcessor.class).value());
                        }
                    }
                }
            }

            for (Map.Entry<IASMDataProcessor, LoaderState[]> entry : dataMap.entrySet()) {
                LoaderState[] hS = entry.getValue();
                if (hS == null || hS.length == 0) {
                    throw new IllegalArgumentException("Invalid LoaderState parameters: Null or empty array; For " + data.getClassName());
                }
                for (LoaderState state : hS) {
                    if (!validStates.contains(state)) {
                        throw new IllegalArgumentException("Invalid LoaderState parameter: " + state + "; For " + data.getClassName());
                    }
                    asmLoaderMap.get(state).add(entry.getKey());
                }
            }
        }
    }

    void process(LoaderState state){
        if (validStates.contains(state)){
            List<IASMDataProcessor> dataProcessors = asmLoaderMap.get(state);
            for (IASMDataProcessor dataProcessor : dataProcessors){
                dataProcessor.processASMData(asmDataHelper, state);
            }
            asmLoaderMap.remove(state);
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static class AdvancedASMData implements IAdvancedASMData {

        private AdvancedASMData(ASMDataTable.ASMData asmData){
            this.asmData = asmData;
            this.isField = asmData.getObjectName().indexOf('(') == -1;
            this.isClass = asmData.getObjectName().indexOf('.') != -1;
            this.annotationInfo = Collections.unmodifiableMap(asmData.getAnnotationInfo());
        }

        private final ASMDataTable.ASMData asmData;
        private final Map<String, Object> annotationInfo;
        private final boolean isField, isClass;
        private Class<?> clazz;
        private Field field;
        private String methodName, methodParams;
        private Method method;
        private Type[] paramTypes;
        private Class[] params;

        @Override
        public ModCandidate getContainer() {
            return asmData.getCandidate();
        }

        @Override
        public String getAnnotationName() {
            return asmData.getAnnotationName();
        }

        @Override
        public Map<String, Object> getAnnotationInfo() {
            return annotationInfo;
        }

        @Override
        public String getClassName() {
            return asmData.getClassName();
        }

        @Override
        public Class<?> loadClass() {
            if (clazz != null){
                return clazz;
            }
            try {
                return clazz = FMLUtil.loadClass(asmData.getClassName());
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public boolean isField() {
            return isField && !isClass;
        }

        @Override
        public String getFieldName() {
            if (!isField()){
                throw new IllegalAccessError();
            }
            return asmData.getObjectName();
        }

        @Override
        public Field getField() {
            if (field != null){
                return field;
            }
            if (!isField()){
                throw new IllegalAccessError();
            }
            try {
                field = loadClass().getDeclaredField(getFieldName());
                field.setAccessible(true);
                return field;
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public Class<?> getFieldType() {
            return getField().getType();
        }

        @Override
        public boolean isMethod() {
            return !isField && !isClass;
        }

        @Override
        public String getMethodName() {
            if (!isMethod()){
                throw new IllegalAccessError();
            }
            if (methodName == null){
                String targetName = asmData.getObjectName();
                int i = targetName.indexOf('('), i2 = targetName.indexOf(')');
                methodName = targetName.substring(0, i);
                if (i2 - i == 1){
                    methodParams = "";
                    paramTypes = new Type[0];
                    params = new Class[0];
                } else {
                    methodParams = targetName.substring(i, i2 + 1);
                }
            }
            return methodName;
        }

        @Override
        public Method getMethod() {
            if (method != null){
                return method;
            }
            if (!isMethod()){
                throw new IllegalAccessError();
            }
            try {
                method = loadClass().getMethod(getMethodName(), getMethodParameters());
                method.setAccessible(true);
                return method;
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }

        @Override
        public Type[] getMethodParameterTypes() {
            if (methodParams == null){
                getAnnotationName();
            }
            if (paramTypes != null){
                return paramTypes;
            }
            return paramTypes = Type.getArgumentTypes(methodParams);
        }

        @Override
        public Class<?>[] getMethodParameters() {
            if (params != null){
                return params;
            }
            if (!isMethod()){
                throw new IllegalAccessError();
            }
            Type[] p = getMethodParameterTypes();
            Class<?>[] ret = new Class<?>[p.length];
            try {
                for (int i = 0; i < p.length; i++) {
                    ret[i] = Class.forName(p[i].getClassName());
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
            return params = ret;
        }

    }
}
