package elec332.core.loader;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IASMDataProcessor;
import elec332.core.api.discovery.IAdvancedASMData;
import elec332.core.util.FMLUtil;
import elec332.core.util.ReflectionHelper;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.ModContainer;
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
@ASMDataProcessor(value = LoaderState.CONSTRUCTING, importance = Integer.MAX_VALUE - 8)
enum APIHandler implements IASMDataProcessor, IAPIHandler {

    INSTANCE;

    APIHandler() {
        callBacks = Maps.newHashMap();
        injectedHandlers = Maps.newHashMap();
    }

    //Used to be a multimap, but multimaps sort contents themselves,
    //we do not want that to happen, because they will be inserted in order
    private final Map<Class<?>, List<Consumer<?>>> callBacks;
    private final Map<Class<?>, Object> injectedHandlers;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {

        getWeightedAdvancedAnnotationList(asmData, StaticLoad.class, "weight").forEach(slD -> {
            try {
                slD.loadClass();
            } catch (Exception e) {
                if (slD.hasSideOnlyAnnotation()) {
                    return;
                }
                throw e;
            }
        });

        collect(asmData, APIHandlerInject.class, "weight");

        inject(INSTANCE, IAPIHandler.class);
    }

    @SuppressWarnings("all")
    private void collect(IASMDataHelper asmData, Class<? extends Annotation> annotationClass, String weightField) {
        bpl:
        for (IAdvancedASMData data : getWeightedAdvancedAnnotationList(asmData, annotationClass, weightField)) {

            try {
                Consumer<?> ret;
                Class<?> type;

                if (data.isMethod()) {
                    Class[] params = data.getMethodParameters();
                    if (params.length > 1 || params.length < 0) {
                        ElecCore.logger.error("Skipping invalid API method: " + data.getClassName() + " " + data.getMethodName());
                    }
                    type = params[0];
                    final Method m = data.getMethod();
                    ret = (Consumer<Object>) o -> {
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
                    final Field field = data.getField();
                    ret = (Consumer<Object>) o -> {
                        List<Object> clsz = tryGetFieldOwner(field);
                        if (clsz == null) {
                            ElecCore.logger.error("Field " + data.getClassName() + "." + data.getFieldName() + " is not accessible! it will be skipped...");
                            return;
                        }
                        clsz.forEach(obj -> {
                            try {
                                EnumHelper.setFailsafeFieldValue(field, obj, o);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    };
                }

                List<Consumer<?>> l = callBacks.computeIfAbsent(Preconditions.checkNotNull(type), t -> Lists.newArrayList());
                l.add(Preconditions.checkNotNull(ret));

            } catch (Exception e) {
                if (data.hasSideOnlyAnnotation()) {
                    continue bpl;
                }
                throw e;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inject(Object o, Class<?>... classes) {
        for (Class<?> clazz : classes) {
            if (!clazz.isAssignableFrom(o.getClass())) {
                throw new IllegalArgumentException();
            }
            for (Consumer consumer : Optional.ofNullable(callBacks.remove(clazz)).orElse(ImmutableList.of())) {
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
    private Collection<IAdvancedASMData> getWeightedAdvancedAnnotationList(IASMDataHelper asmData, Class<? extends Annotation> annotationClass, String weightField) {
        return asmData.getAdvancedAnnotationList(annotationClass).stream().sorted((o1, o2) -> {
            int ic = Strings.isNullOrEmpty(weightField) ? 0 : Comparator.comparingInt((ToIntFunction<IAdvancedASMData>) value -> Math.abs((int) ObjectUtils.firstNonNull(value.getAnnotationInfo().get(weightField), (int) Byte.MAX_VALUE))).compare(o1, o2);
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
        ModContainer mc = FMLUtil.getOwner(owner);
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
