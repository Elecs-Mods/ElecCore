package elec332.core.main;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IElecCoreMod;
import elec332.core.api.data.IExternalSaveHandler;
import elec332.core.api.module.IModuleContainer;
import elec332.core.api.network.INetworkManager;
import elec332.core.api.network.ModNetworkHandler;
import elec332.core.api.registry.ISingleRegister;
import elec332.core.module.ModuleManager;
import elec332.core.server.SaveHandler;
import elec332.core.util.CommandHelper;
import elec332.core.util.FMLUtil;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 20-10-2016.
 */
public class ElecModHandler {

    @APIHandlerInject
    private static INetworkManager networkManager;
    private static List<Pair<ModContainer, IElecCoreMod>> mods;

    static void identifyMods(){
        for (ModContainer mc : FMLUtil.getLoader().getActiveModList()){
            Object o = mc.getMod();
            if (o instanceof ElecCore){  //Primary handler, always goes first
                mods.add(0, Pair.of(mc, (IElecCoreMod) o));
            } else if (o instanceof IElecCoreMod){
                mods.add(Pair.of(mc, (IElecCoreMod) o));
            }
        }

    }

    static void init(){
        init(mods);
        List<Pair<ModContainer, IElecCoreMod>> modules = Lists.newArrayList();
        for (IModuleContainer moduleContainer : ModuleManager.INSTANCE.getActiveModules()){
            if (moduleContainer.getModule() instanceof IElecCoreMod){
                modules.add(Pair.of(moduleContainer.getOwnerMod(), (IElecCoreMod) moduleContainer.getModule()));
            }
        }
        init(modules);
    }

    private static void init(List<Pair<ModContainer, IElecCoreMod>> mods){
        for (Pair<ModContainer, IElecCoreMod> p : mods){
            IElecCoreMod mod = p.getRight();
            ModContainer mc = p.getLeft();
            mod.registerClientCommands(CommandHelper.getClientCommandRegistry());
            mod.registerServerCommands(CommandHelper.getServerCommandRegistry());
            mod.registerSaveHandlers(new ISingleRegister<IExternalSaveHandler>() {

                @Override
                public boolean register(IExternalSaveHandler iExternalSaveHandler) {
                    return SaveHandler.INSTANCE.registerSaveHandler(mc, Preconditions.checkNotNull(iExternalSaveHandler));
                }

            });

        }
    }

    private static void registerSimpleFieldHandlers(Map<Class<? extends Annotation>, Function<ModContainer, Object>> registry){
        registry.put(ModNetworkHandler.class, new Function<ModContainer, Object>() {

            @Nullable
            @Override
            public Object apply(@Nullable ModContainer input) {
                return networkManager.getNetworkHandler(input);
            }

        });
    }

    static void initAnnotations(ASMDataTable dataTable){
        Map<Class<? extends Annotation>, Function<ModContainer, Object>> reg = Maps.newHashMap();
        registerSimpleFieldHandlers(reg);
        for (Map.Entry<Class<? extends Annotation>, Function<ModContainer, Object>> entry : reg.entrySet()){
            for (ModContainer mc: FMLUtil.getLoader().getActiveModList()){
                parseSimpleFieldAnnotation(mc, dataTable.getAnnotationsFor(mc), entry.getKey().getName(), entry.getValue());
            }
        }

    }

    private static void parseSimpleFieldAnnotation(ModContainer mc_, SetMultimap<String, ASMDataTable.ASMData> annotations, String annotationClassName, Function<ModContainer, Object> retriever){
        try {
            String[] annName = annotationClassName.split("\\.");
            String annotationName = annName[annName.length - 1];
            for (ASMDataTable.ASMData targets : annotations.get(annotationClassName))
            {
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

        }
    }

    static {
        mods = Lists.newArrayList();
    }

}
