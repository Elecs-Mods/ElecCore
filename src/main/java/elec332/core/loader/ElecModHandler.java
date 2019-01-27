package elec332.core.loader;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.discovery.IAnnotationData;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.mod.IElecCoreMod;
import elec332.core.api.mod.IElecCoreModHandler;
import elec332.core.util.FMLHelper;
import net.minecraftforge.fml.ModContainer;
import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.Type;

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

    ElecModHandler() {
        reg = Maps.newHashMap();
        handlers = Sets.newHashSet();
    }

    @APIHandlerInject(weight = Short.MAX_VALUE)
    private IAnnotationDataHandler asmData = null;
    private Map<Class<? extends Annotation>, Function<ModContainer, Object>> reg;
    private Set<BiConsumer<ModContainer, IElecCoreMod>> handlers;
    private List<Pair<ModContainer, IElecCoreMod>> mods;

    void afterConstruct() {
        identifyMods();
        initAnnotations(asmData);
        init();
    }

    private void identifyMods() {
        mods = FMLHelper.getMods().stream()
                .filter(o -> o.getMod() instanceof IElecCoreMod)
                .sorted((o1, o2) -> o1.getMod() instanceof ElecCore ? 1 : (o2.getMod() instanceof ElecCore ? -1 : Integer.compare(o1.hashCode(), o2.hashCode())))
                .map(modContainer -> Pair.of(modContainer, (IElecCoreMod) modContainer.getMod()))
                .peek(mcp -> {
                    if (!FMLHelper.hasFMLModContainer(mcp.getLeft())){
                        ElecCore.logger.error("ModContainer " + mcp.getLeft() + " isn't instanceof FMLModContainer!");
                        ElecCore.logger.error("This will limit some functionality.");
                    }
                })
                .collect(Collectors.toList());
    }

    private void init() {
        init(mods);
        init(ModuleManager.INSTANCE.getActiveModules().stream()
                .filter(mc -> mc.getModule() instanceof IElecCoreMod)
                .map(moduleContainer -> Pair.of(moduleContainer.getOwnerMod(), (IElecCoreMod) moduleContainer.getModule()))
                .collect(Collectors.toList()));
    }

    private void init(List<Pair<ModContainer, IElecCoreMod>> mods) {
        mods.forEach(mod -> handlers.forEach(handler -> handler.accept(mod.getLeft(), mod.getRight())));
    }

    private void initAnnotations(IAnnotationDataHandler dataTable) {
        for (Map.Entry<Class<? extends Annotation>, Function<ModContainer, Object>> entry : reg.entrySet()) {
            for (ModContainer mc : FMLHelper.getMods()) {
                parseSimpleFieldAnnotation(mc, dataTable.getAnnotationsFor(mc), Type.getType(entry.getKey()), entry.getValue());
            }
        }
    }

    private void parseSimpleFieldAnnotation(ModContainer mc_, Function<Type, Set<IAnnotationData>> annotations, Type annotationType, Function<ModContainer, Object> retriever) {
        try {
            for (IAnnotationData targets : annotations.apply(annotationType)) {
                String targetMod = (String) targets.getAnnotationInfo().get("value");
                String annotationName = annotationType.getClassName();
                Field f = null;
                Object injectedMod = null;
                ModContainer mc = mc_;
                boolean isStatic = false;
                Class<?> clz = mc_.getMod().getClass();
                if (!Strings.isNullOrEmpty(targetMod)) {
                    if (FMLHelper.isModLoaded(targetMod)) {
                        mc = FMLHelper.getModList().getModContainerById(targetMod).orElse(null);
                    } else {
                        mc = null;
                    }
                }

                if (mc != null) {
                    try {
                        f = targets.getField();
                        f.setAccessible(true);
                        isStatic = Modifier.isStatic(f.getModifiers());
                        injectedMod = retriever.apply(mc);
                    } catch (Exception e) {
                        ElecCore.logger.error(String.format("Attempting to load @%s in class %s for %s and failing", annotationName, targets.getClassName(), mc.getModId()), e);
                        e.printStackTrace();
                        throw e;
                    }
                }
                if (f != null) {
                    Object target = null;
                    if (!isStatic) {
                        target = mc_.getMod();
                        if (!target.getClass().equals(clz)) {
                            ElecCore.logger.error(String.format("Unable to inject @%s in non-static field %s.%s for %s as it is NOT the primary mod instance", annotationName, targets.getClassName(), targets.getMemberName(), mc.getModId()));
                            continue;
                        }
                    }
                    f.set(target, injectedMod);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
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

    @APIHandlerInject(weight = Short.MAX_VALUE)
    public void injectModHandler(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, IElecCoreModHandler.class);
    }

}
