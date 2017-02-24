package elec332.core.main;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IASMDataProcessor;
import elec332.core.api.discovery.IAdvancedASMData;
import elec332.core.api.registration.NamedFieldGetter;
import elec332.core.api.world.IWorldGenManager;
import elec332.core.java.ReflectionHelper;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.LoaderState;

import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Created by Elec332 on 29-10-2016.
 */
@ASMDataProcessor(LoaderState.CONSTRUCTING)
public enum APIHandler implements IASMDataProcessor, IAPIHandler {

    INSTANCE;

    APIHandler(){
        callBacks = HashMultimap.create();
    }

    private final SetMultimap<Class<?>, Consumer<?>> callBacks;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        collect(asmData, APIHandlerInject.class);

        inject(INSTANCE, IAPIHandler.class);

        inject(ElecCore.instance.asmDataProcessor.asmDataHelper, IASMDataHelper.class);

        for (IAdvancedASMData data : asmData.getAdvancedAnnotationList(StaticLoad.class)){
            data.loadClass();
        }

        injectFields(asmData);

    }

    private void collect(IASMDataHelper asmData, Class<? extends Annotation> annotationClass){
        for (IAdvancedASMData data : asmData.getAdvancedAnnotationList(annotationClass)){

            Consumer<?> ret;
            Class<?> type;

            if (data.isMethod()){
                Class[] params = data.getMethodParameters();
                if (params.length > 1 || params.length < 0){
                    ElecCore.logger.error("Skipping invalid API method: "+data.getClassName() + " "+ data.getMethodName());
                }
                type = params[0];
                ret = new Consumer<Object>() {

                    @Override
                    public void accept(Object o) {
                        if (!ReflectionHelper.isStatic(data.getMethod())){
                            ElecCore.logger.error("Method "+data.getClassName() + " "+ data.getMethodName()+" is not static! it will be skipped...");
                            return;
                        }
                        try {
                            data.getMethod().invoke(null, o);
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }

                };
            } else {
                type = data.getFieldType();
                ret = new Consumer<Object>() {

                    @Override
                    public void accept(Object o) {
                        if (!ReflectionHelper.isStatic(data.getField())){
                            ElecCore.logger.error("Field "+data.getClassName() + " "+ data.getFieldName()+" is not static! it will be skipped...");
                            return;
                        }
                        try {
                            EnumHelper.setFailsafeFieldValue(data.getField(), null, o);
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }

                };
            }

            callBacks.put(Preconditions.checkNotNull(type), Preconditions.checkNotNull(ret));

        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void inject(Object o, Class<?>... classes){
        for (Class<?> clazz : classes){
            if (!clazz.isAssignableFrom(o.getClass())){
                throw new IllegalArgumentException();
            }
            for (Consumer consumer : callBacks.removeAll(clazz)){
                consumer.accept(o);
            }
        }
    }

    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface StaticLoad {
    }

    @APIHandlerInject
    static IWorldGenManager worldGenManager;

    private static void injectFields(IASMDataHelper asmData){
        Map<Class, NamedFieldGetter.Definition> definitionMap = Maps.newHashMap();
        Map<NamedFieldGetter, Field> namedFieldGetters = Maps.newHashMap();
        for (IAdvancedASMData ad : asmData.getAdvancedAnnotationList(NamedFieldGetter.Definition.class)){
            Class c = ad.loadClass();
            definitionMap.put(c, (NamedFieldGetter.Definition) c.getAnnotation(NamedFieldGetter.Definition.class));
        }
        for (IAdvancedASMData ad : asmData.getAdvancedAnnotationList(NamedFieldGetter.class)){
            Field c = ad.getField();
            namedFieldGetters.put((NamedFieldGetter) c.getAnnotation(NamedFieldGetter.class), c);
        }
        for (Map.Entry<NamedFieldGetter, Field> entry : namedFieldGetters.entrySet()){
            Field f = entry.getValue();
            NamedFieldGetter nfg = entry.getKey();
            NamedFieldGetter.Definition definition = definitionMap.get(entry.getValue().getDeclaringClass());
            String field = nfg.field();
            if (Strings.isNullOrEmpty(field)){
                field = definition == null ? null : definition.field();
                if (Strings.isNullOrEmpty(field)){
                    throw new RuntimeException("Class: "+entry.getValue().getDeclaringClass()+" field: "+ entry.getValue());
                }
            }
            Class dec = nfg.declaringClass();
            if (dec == Object.class){
                dec = definition == null ? Object.class : definition.declaringClass();
                if (dec == Object.class){
                    throw new RuntimeException("Class: "+entry.getValue().getDeclaringClass()+" field: "+ entry.getValue());
                }
            }
            try {
                String[] poss = field.split(",");
                Field nameField = null;
                for (String s : poss){
                    try {
                        nameField = f.getType().getDeclaredField(s);
                        break;
                    } catch (Exception e){
                        //
                    }
                }
                if (nameField == null){
                    throw new RuntimeException("Unable to find: "+field);
                }
                for (Field fDec : dec.getDeclaredFields()) {
                    if (fDec.getType() == f.getType()) {
                        Object o = fDec.get(null);
                        String name = (String) nameField.get(o);
                        if (name.equals(nfg.name())) {
                            f.set(null, o);
                        }
                    }
                }
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }

}
