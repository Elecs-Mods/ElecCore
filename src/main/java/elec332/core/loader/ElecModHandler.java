package elec332.core.loader;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.IElecCoreModHandler;
import elec332.core.util.FMLUtil;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 20-10-2016.
 */
@StaticLoad
enum ElecModHandler implements IElecCoreModHandler {

    INSTANCE;

    ElecModHandler(){
        reg = Maps.newHashMap();
        handlers = Sets.newHashSet();
    }

    @APIHandlerInject(weight = Short.MAX_VALUE)
    private IASMDataHelper asmData = null;
    private Map<Class<? extends Annotation>, Function<ModContainer, Object>> reg;
    private Set<BiConsumer<ModContainer, IElecCoreMod>> handlers;
    private List<Pair<ModContainer, IElecCoreMod>> mods;

    void latePreInit(){
        identifyMods();
        initAnnotations(asmData.getASMDataTable());
        init();
    }

    private void identifyMods(){
        mods = FMLUtil.getLoader().getActiveModList().stream()
                .filter(o -> o.getMod() instanceof IElecCoreMod)
                .sorted((o1, o2) -> o1.getMod() instanceof ElecCore ? 1 :(o2.getMod() instanceof ElecCore ? -1 : Integer.compare(o1.hashCode(), o2.hashCode())))
                .map(modContainer -> Pair.of(modContainer, (IElecCoreMod) modContainer.getMod()))
                .collect(Collectors.toList());
    }

    private void init(){
        init(mods);
        init(ModuleManager.INSTANCE.getActiveModules().stream()
                .filter(mc -> mc.getModule() instanceof IElecCoreMod)
                .map(moduleContainer -> Pair.of(moduleContainer.getOwnerMod(), (IElecCoreMod) moduleContainer.getModule()))
                .collect(Collectors.toList()));
    }

    private void init(List<Pair<ModContainer, IElecCoreMod>> mods){
        mods.forEach(mod -> handlers.forEach(handler -> handler.accept(mod.getLeft(), mod.getRight())));
    }

    private void initAnnotations(ASMDataTable dataTable){
        for (Map.Entry<Class<? extends Annotation>, Function<ModContainer, Object>> entry : reg.entrySet()){
            for (ModContainer mc: FMLUtil.getLoader().getActiveModList()){
                parseSimpleFieldAnnotation(mc, dataTable.getAnnotationsFor(mc), entry.getKey().getName(), entry.getValue());
            }
        }
    }

    private void parseSimpleFieldAnnotation(ModContainer mc_, SetMultimap<String, ASMDataTable.ASMData> annotations, String annotationClassName, Function<ModContainer, Object> retriever){
        try {
            String[] annName = annotationClassName.split("\\.");
            String annotationName = annName[annName.length - 1];
            for (ASMDataTable.ASMData targets : annotations.get(annotationClassName)) {
                String targetMod = (String)targets.getAnnotationInfo().get("value");
                Field f = null;
                Object injectedMod = null;
                ModContainer mc = mc_;
                boolean isStatic = false;
                Class<?> clz = mc_.getMod().getClass();
                if (!Strings.isNullOrEmpty(targetMod)) {
                    if (Loader.isModLoaded(targetMod)) {
                        mc = Loader.instance().getIndexedModList().get(targetMod);
                    } else {
                        mc = null;
                    }
                }

                if (mc != null) {
                    try {
                        clz = Class.forName(targets.getClassName(), true, Loader.instance().getModClassLoader());
                        f = clz.getDeclaredField(targets.getObjectName());
                        f.setAccessible(true);
                        isStatic = Modifier.isStatic(f.getModifiers());
                        injectedMod = retriever.apply(mc);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Throwables.propagateIfPossible(e);
                        FMLLog.log(mc_.getModId(), Level.WARN, e, "Attempting to load @%s in class %s for %s and failing", annotationName, targets.getClassName(), mc.getModId());
                    }
                }
                if (f != null) {
                    Object target = null;
                    if (!isStatic) {
                        target = mc_.getMod();
                        if (!target.getClass().equals(clz)) {
                            FMLLog.log(mc_.getModId(), Level.WARN, "Unable to inject @%s in non-static field %s.%s for %s as it is NOT the primary mod instance", annotationName, targets.getClassName(), targets.getObjectName(), mc.getModId());
                            continue;
                        }
                    }
                    f.set(target, injectedMod);
                }
            }
        } catch (Exception e){
            //
        }
    }

    @Override
    public void registerSimpleFieldHandler(Class<? extends Annotation> annotation, Function<ModContainer, Object> func) {
        reg.put(annotation, func);
    }

    @Override
    public void registerModHandler(BiConsumer<ModContainer, IElecCoreMod> handler) {
        handlers.add(handler);
    }

    @APIHandlerInject()
    public void injectModHandler(IAPIHandler apiHandler){
        apiHandler.inject(INSTANCE, IElecCoreModHandler.class);
    }

}
