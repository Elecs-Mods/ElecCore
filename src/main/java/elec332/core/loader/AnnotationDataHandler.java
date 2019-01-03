package elec332.core.loader;

import com.google.common.collect.*;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.util.FMLHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.language.ModFileScanData;
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.fml.loading.moddiscovery.ModFileInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 29-10-2016.
 */
enum AnnotationDataHandler {

    INSTANCE;

    AnnotationDataHandler() {
        asmLoaderMap = Maps.newHashMap();
        validStates = ImmutableList.of(ModLoadingStage.CONSTRUCT, ModLoadingStage.PREINIT, ModLoadingStage.INIT, ModLoadingStage.POSTINIT, ModLoadingStage.COMPLETE);
    }

    private final Map<ModLoadingStage, List<IAnnotationDataProcessor>> asmLoaderMap;
    private final List<ModLoadingStage> validStates;
    private IAnnotationDataHandler asmDataHelper;

    void identify(ModList modList) {
        for (ModLoadingStage state : validStates) {
            asmLoaderMap.put(state, Lists.newArrayList());
        }
        final SetMultimap<Type, IAnnotationData> annotationData = HashMultimap.create();
        final Map<Path, SetMultimap<Type, IAnnotationData>> annotationDataF = modList.getModFiles().stream()
                .map(ModFileInfo::getFile)
                .collect(Collectors.toMap(ModFile::getFilePath, mf -> {
                    SetMultimap<Type, IAnnotationData> ret = HashMultimap.create();
                    mf.getScanResult().getAnnotations().forEach(
                            ad -> annotationData.put(ad.getAnnotationType(), new AnnotationData(ad, mf))
                    );
                    return ret;
                }));
        annotationDataF.values().forEach(annotationData::putAll);
        this.asmDataHelper = new IAnnotationDataHandler() {

            @Override
            public Set<IAnnotationData> getAnnotationList(Type annotationType) {
                Set<IAnnotationData> ret = annotationData.get(annotationType);
                return ret == null ? ImmutableSet.of() : Collections.unmodifiableSet(ret);
            }

            @Override
            public Function<Type, Set<IAnnotationData>> getAnnotationsFor(ModContainer mc) {
                return getAnnotationsFor(((ModFileInfo) mc.getModInfo().getOwningFile()).getFile().getFilePath());
            }

            @Override
            public Function<Type, Set<IAnnotationData>> getAnnotationsFor(Path file) {
                return type -> Optional.of(annotationDataF.get(file).get(type)).orElse(ImmutableSet.of());
            }

            @Override
            public String deepSearchOwner(IAnnotationData annotationData) {
                //Todo: advanced search
                ModFile owner = annotationData.getFile();
                if (owner.getModInfos().size() == 1) {
                    return owner.getModInfos().get(0).getModId();
                }
                return null;
            }

        };

        Map<Pair<Integer, IAnnotationDataProcessor>, ModLoadingStage[]> dataMap = Maps.newTreeMap(Comparator.comparing((Function<Pair<Integer, IAnnotationDataProcessor>, Integer>) Pair::getKey).reversed().thenComparing(Object::hashCode));

        for (IAnnotationData data : asmDataHelper.getAnnotationList(AnnotationDataProcessor.class)) {
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
            if (clazz.isAnnotationPresent(AnnotationDataProcessor.class)) {
                AnnotationDataProcessor annData = clazz.getAnnotation(AnnotationDataProcessor.class);
                ModLoadingStage[] ls = annData.value();
                int importance = annData.importance();
                if (clazz.isEnum()) {
                    for (Object e : clazz.getEnumConstants()) {
                        if (e instanceof IAnnotationDataProcessor) {
                            dataMap.put(Pair.of(importance, (IAnnotationDataProcessor) e), ls);
                        }
                    }
                    eb = true;
                } else {
                    Object o;
                    try {
                        o = clazz.newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException("Error invocating annotated IASMData class: " + data.getClassName(), e);
                    }
                    if (o instanceof IAnnotationDataProcessor) {
                        dataMap.put(Pair.of(importance, (IAnnotationDataProcessor) o), ls);
                    }
                }
            }

            if (!eb) {
                for (Field field : clazz.getDeclaredFields()) {
                    if (field.isAnnotationPresent(AnnotationDataProcessor.class)) {
                        Object obj;
                        try {
                            obj = field.get(null);
                        } catch (Exception e) {
                            continue; //Not static
                        }
                        if (obj instanceof IAnnotationDataProcessor) {
                            AnnotationDataProcessor annData = field.getAnnotation(AnnotationDataProcessor.class);
                            dataMap.put(Pair.of(annData.importance(), (IAnnotationDataProcessor) obj), annData.value());
                        }
                    }
                }
            }

        }

        for (Map.Entry<Pair<Integer, IAnnotationDataProcessor>, ModLoadingStage[]> entry : dataMap.entrySet()) {
            ModLoadingStage[] hS = entry.getValue();
            if (hS == null || hS.length == 0) {
                throw new IllegalArgumentException("Invalid ModLoadingStage parameters: Null or empty array; For " + entry.getKey().getValue().getClass());
            }
            for (ModLoadingStage state : hS) {
                if (!validStates.contains(state)) {
                    throw new IllegalArgumentException("Invalid ModLoadingStage parameter: " + state + "; For " + entry.getKey().getValue().getClass());
                }
                asmLoaderMap.get(state).add(entry.getKey().getValue());
            }
        }
    }

    void process(ModLoadingStage state) {
        if (validStates.contains(state)) {
            List<IAnnotationDataProcessor> dataProcessors = asmLoaderMap.get(state);
            for (IAnnotationDataProcessor dataProcessor : dataProcessors) {
                dataProcessor.processASMData(asmDataHelper, state);
            }
            asmLoaderMap.remove(state);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @APIHandlerInject(weight = 1)
    public void injectASMHelper(IAPIHandler apiHandler) {
        apiHandler.inject(this.asmDataHelper, IAnnotationDataHandler.class);
    }

    private static class AnnotationData implements IAnnotationData {

        private AnnotationData(ModFileScanData.AnnotationData asmData, ModFile file) {
            this.asmData = asmData;
            this.modFile = file;
            this.isField = asmData.getMemberName().indexOf('(') == -1;
            this.isClass = asmData.getMemberName().indexOf('.') != -1;
            this.annotationInfo = Collections.unmodifiableMap(asmData.getAnnotationData());
        }

        private final ModFile modFile;
        private final ModFileScanData.AnnotationData asmData;
        private final Map<String, Object> annotationInfo;
        private final boolean isField, isClass;
        private Class<?> clazz;
        private Field field;
        private String methodName, methodParams;
        private Method method;
        private Type[] paramTypes;
        private Class[] params;

        @Override
        public ModFile getFile() {
            return modFile;
        }

        @Override
        public Type getAnnotationType() {
            return asmData.getAnnotationType();
        }

        @Override
        public Map<String, Object> getAnnotationInfo() {
            return annotationInfo;
        }

        @Override
        public Class<?> loadClass() {
            if (clazz != null) {
                return clazz;
            }
            try {
                return clazz = FMLHelper.loadClass(getClassName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Type getClassType() {
            return asmData.getClassType();
        }

        @Override
        public String getMemberName() {
            return asmData.getMemberName();
        }

        @Override
        public boolean isField() {
            return isField && !isClass;
        }

        @Override
        public String getFieldName() {
            if (!isField()) {
                throw new IllegalAccessError();
            }
            return asmData.getMemberName();
        }

        @Override
        public Field getField() {
            if (field != null) {
                return field;
            }
            if (!isField()) {
                throw new IllegalAccessError();
            }
            try {
                field = loadClass().getDeclaredField(getFieldName());
                field.setAccessible(true);
                return field;
            } catch (Exception e) {
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
            if (!isMethod()) {
                throw new IllegalAccessError();
            }
            if (methodName == null) {
                String targetName = asmData.getMemberName();
                int i = targetName.indexOf('('), i2 = targetName.indexOf(')');
                methodName = targetName.substring(0, i);
                if (i2 - i == 1) {
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
            if (method != null) {
                return method;
            }
            if (!isMethod()) {
                throw new IllegalAccessError();
            }
            try {
                //todo: sort out why existing methods don't exist
                System.out.println(Lists.newArrayList(loadClass().getDeclaredMethods()));
                Arrays.stream(loadClass().getDeclaredMethods()).forEach(m -> {
                    System.out.println(m.getName() + "  " + Lists.newArrayList(m.getParameterTypes()));
                });
                method = loadClass().getDeclaredMethod(getMethodName(), getMethodParameters());
                method.setAccessible(true);
                return method;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Type[] getMethodParameterTypes() {
            if (methodParams == null) {
                getMethodName();
            }
            if (paramTypes != null) {
                return paramTypes;
            }
            return paramTypes = Type.getArgumentTypes(methodParams);
        }

        @Override
        public Class<?>[] getMethodParameters() {
            if (params != null) {
                return params;
            }
            if (!isMethod()) {
                throw new IllegalAccessError();
            }
            Type[] p = getMethodParameterTypes();
            Class<?>[] ret = new Class<?>[p.length];
            try {
                for (int i = 0; i < p.length; i++) {
                    ret[i] = Class.forName(p[i].getClassName());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return params = ret;
        }

        @Override
        public String toString() {
            return ""
                    + " Annotation:" + getAnnotationName()
                    + " Class:" + getClassName()
                    + " Field name:" + (isField() ? getFieldName() : "-")
                    + " Method name:" + (isMethod() ? getMethodName() : "-")
                    + " Annotation data:" + getAnnotationInfo();
        }
    }
}
