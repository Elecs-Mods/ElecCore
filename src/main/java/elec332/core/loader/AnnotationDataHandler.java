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
import net.minecraftforge.fml.loading.moddiscovery.ModFile;
import net.minecraftforge.forgespi.language.IModFileInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
        validStates = ImmutableList.of(ModLoadingStage.CONSTRUCT, ModLoadingStage.COMMON_SETUP, ModLoadingStage.ENQUEUE_IMC, ModLoadingStage.PROCESS_IMC, ModLoadingStage.COMPLETE);
    }

    private final Map<ModLoadingStage, List<IAnnotationDataProcessor>> asmLoaderMap;
    private final List<ModLoadingStage> validStates;
    private Function<String, ModContainer> packageOwners;
    private Set<String> packSorted;
    private IAnnotationDataHandler asmDataHelper;

    void identify(ModList modList) {
        for (ModLoadingStage state : validStates) {
            asmLoaderMap.put(state, Lists.newArrayList());
        }

        Map<String, ModContainer> pck = Maps.newTreeMap((o1, o2) -> {
            if (o2.contains(o1)){
                return 1;
            }
            if (o1.contains(o2)){
                return -1;
            }
            return 0;
        });

        FMLHelper.getMods().forEach(mc -> {
            if (mc.getMod() != null) {
                String pack = mc.getMod().getClass().getCanonicalName();
                pack = pack.substring(0, pack.lastIndexOf("."));
                pck.put(pack, mc);
            }
        });

        Map<String, ModContainer> pck2 = Maps.newHashMap(pck);

        packSorted = pck.keySet();
        packageOwners = pck2::get;

        Function<IAnnotationData, ModContainer> modSearcher = annotationData -> {
            if (annotationData.getClassName().startsWith("net.minecraft.") || annotationData.getClassName().startsWith("mcp.")){
                return null;//FMLHelper.getModList().getModContainerById(DefaultModInfos.minecraftModInfo.getModId()).orElseThrow(NullPointerException::new);
            }
            ModFile owner = annotationData.getFile();
            if (owner.getModInfos().size() == 1) {
                return FMLHelper.getModList().getModContainerById(owner.getModInfos().get(0).getModId()).orElseThrow(RuntimeException::new);
            }
            String pack = packSorted.stream().filter(s -> annotationData.getClassName().contains(s)).findFirst().orElseThrow(RuntimeException::new);
            return packageOwners.apply(pack);
        };

        final Map<String, SetMultimap<Type, IAnnotationData>> annotationDataM = Maps.newHashMap();
        final Map<IModFileInfo, SetMultimap<Type, IAnnotationData>> annotationDataF = modList.getModFiles().stream()
                .collect(Collectors.toMap(Function.identity(), mfi -> {
                    ModFile mf = mfi.getFile();
                    SetMultimap<Type, IAnnotationData> ret = HashMultimap.create();
                    mf.getScanResult().getAnnotations().forEach(ad -> {
                        IAnnotationData annotationData = new AnnotationData(ad, mf);
                        if (annotationData.getAnnotationName().startsWith("Ljava/lang") || annotationData.getAnnotationName().startsWith("Ljavax/annotation")){
                            return;
                        }
                        Type annType = ad.getAnnotationType();
                        ret.put(annType, annotationData);
                        ModContainer mc = modSearcher.apply(annotationData);
                        if (mc != null) {
                            annotationDataM.computeIfAbsent(mc.getModId(), s -> HashMultimap.create()).put(annType, annotationData);
                        }
                    });
                    return ret;
                }));
        final SetMultimap<Type, IAnnotationData> annotationData = HashMultimap.create();
        annotationDataF.values().forEach(annotationData::putAll);

        this.asmDataHelper = new IAnnotationDataHandler() {

            @Override
            public Set<IAnnotationData> getAnnotationList(Type annotationType) {
                Set<IAnnotationData> ret = annotationData.get(annotationType);
                return ret == null ? ImmutableSet.of() : Collections.unmodifiableSet(ret);
            }

            @Override
            public Function<Type, Set<IAnnotationData>> getAnnotationsFor(ModContainer mc) {
                return type -> Optional.ofNullable(annotationDataM.get(mc.getModId())).map(t -> t.get(type)).orElse(ImmutableSet.of());
            }

            @Override
            public Function<Type, Set<IAnnotationData>> getAnnotationsFor(IModFileInfo file) {
                return type -> Optional.ofNullable(annotationDataF.get(file)).map(t -> t.get(type)).orElse(ImmutableSet.of());
            }

            @Override
            public ModContainer deepSearchOwner(IAnnotationData annotationData) {
                return modSearcher.apply(annotationData);
            }

            @Override
            public String deepSearchOwnerName(IAnnotationData annotationData) {
                ModFile owner = annotationData.getFile();
                if (owner.getModInfos().size() == 1) {
                    return owner.getModInfos().get(0).getModId();
                }
                return deepSearchOwner(annotationData).getModId();
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
