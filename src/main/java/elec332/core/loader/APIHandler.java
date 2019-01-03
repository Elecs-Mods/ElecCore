package elec332.core.loader;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import elec332.core.ElecCore;
import elec332.core.api.IAPIHandler;
import elec332.core.api.discovery.AnnotationDataProcessor;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.discovery.IAnnotationDataProcessor;
import elec332.core.util.FMLHelper;
import elec332.core.util.FieldPointer;
import elec332.core.util.ReflectionHelper;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import org.apache.commons.lang3.ObjectUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 29-10-2016.
 */
@AnnotationDataProcessor(value = ModLoadingStage.PREINIT, importance = Integer.MAX_VALUE)
enum APIHandler implements IAnnotationDataProcessor, IAPIHandler {

    INSTANCE;

    APIHandler() {
        callBacks = HashMultimap.create();
        injectedHandlers = Maps.newHashMap();
    }

    private final SetMultimap<Class<?>, Consumer<?>> callBacks;
    private final Map<Class<?>, Object> injectedHandlers;

    @Override
    public void processASMData(IAnnotationDataHandler asmData, ModLoadingStage state) {

        //todo: re-enable
        //getWeightedAdvancedAnnotationList(asmData, StaticLoad.class, "weight").forEach(IAnnotationData::loadClass);

        //collect(asmData, APIHandlerInject.class, "weight");

        inject(INSTANCE, IAPIHandler.class);

    }

    @SuppressWarnings("all")
    private void collect(IAnnotationDataHandler asmData, Class<? extends Annotation> annotationClass, String weightField) {
        for (IAnnotationData data : getWeightedAdvancedAnnotationList(asmData, annotationClass, weightField)) {

            Consumer<?> ret;
            Class<?> type;

            if (data.isMethod()) {
                Class[] params = data.getMethodParameters();
                if (params.length > 1 || params.length < 0) {
                    ElecCore.logger.error("Skipping invalid API method: " + data.getClassName() + " " + data.getMethodName());
                }
                type = params[0];
                ret = (Consumer<Object>) o -> {
                    Method m = data.getMethod();
                    List<Object> clsz = tryGetFieldOwner(m);
                    if (clsz == null) {
                        ElecCore.logger.error("Method " + data.getClassName() + " " + data.getMethodName() + " is not accessible! it will be skipped...");
                        return;
                    }
                    m.setAccessible(true);
                    clsz.forEach(obj -> {
                        try {
                            m.invoke(obj, o);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                };
            } else {
                type = data.getFieldType();
                ret = (Consumer<Object>) o -> {
                    Field field = data.getField();
                    List<Object> clsz = tryGetFieldOwner(field);
                    if (clsz == null) {
                        ElecCore.logger.error("Field " + data.getClassName() + "." + data.getFieldName() + " is not accessible! it will be skipped...");
                        return;
                    }
                    clsz.forEach(obj -> {
                        try {
                            (new FieldPointer<>(field)).set(obj, o);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    });
                };
            }

            callBacks.put(Preconditions.checkNotNull(type), Preconditions.checkNotNull(ret));

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inject(Object o, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException();
            }
            for (Consumer consumer : callBacks.removeAll(clazz)) {
                consumer.accept(o);
            }
            injectedHandlers.put(clazz, o);
        }
    }

    @Nullable
    @Override
    @SuppressWarnings("all")
    public <T> T get(Class<T> type) {
        return (T) injectedHandlers.get(type);
    }

    @SuppressWarnings("all")
    private Collection<IAnnotationData> getWeightedAdvancedAnnotationList(IAnnotationDataHandler asmData, Class<? extends Annotation> annotationClass, String weightField) {
        return asmData.getAnnotationList(annotationClass).stream().sorted((o1, o2) -> {
            int ic = Strings.isNullOrEmpty(weightField) ? 0 : Comparator.comparingInt((ToIntFunction<IAnnotationData>) value -> Math.abs((int) ObjectUtils.firstNonNull(value.getAnnotationInfo().get(weightField), (int) Byte.MAX_VALUE))).compare(o1, o2);
            if (ic == 0) {
                return Integer.compare(o1.hashCode(), o2.hashCode());
            }
            return ic;
        }).collect(Collectors.toList());
    }

    // :(
    private List<Object> tryGetFieldOwner(Member field) {
        if (ReflectionHelper.isStatic(field)) {
            return Lists.newArrayList((Object) null);
        }
        Class owner = field.getDeclaringClass();
        if (owner.isEnum()) {
            return Arrays.asList(owner.getEnumConstants());
        }
        ModContainer mc = FMLHelper.getOwner(owner);
        if (mc != null && mc.getMod().getClass() == owner) {
            return Lists.newArrayList(mc.getMod());
        }
        Field inst = null;
        try {
            inst = owner.getDeclaredField("instance");
        } catch (Exception e) {
            //nup
        }
        if (inst == null) {
            try {
                inst = owner.getDeclaredField("INSTANCE");
            } catch (Exception e) {
                //nup
            }
        }
        if (inst != null && ReflectionHelper.isStatic(inst)) {
            inst.setAccessible(true);
            try {
                return Lists.newArrayList(inst.get(null));
            } catch (Exception e) {
                //???
            }
        }
        return null;
    }

}
