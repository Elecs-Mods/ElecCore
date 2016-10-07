package elec332.core.module;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.annotations.ASMDataProcessor;
import elec332.core.api.util.IASMDataHelper;
import elec332.core.api.util.IASMDataProcessor;
import elec332.core.handler.ModEventHandler;
import elec332.core.main.ElecCore;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Elec332 on 24-9-2016.
 */
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public enum ModuleHandler implements IASMDataProcessor {

    INSTANCE;

    ModuleHandler(){
        this.registeredModules = Sets.newHashSet();
        this.moduleControllers = Maps.newHashMap();
        this.activeModules = Sets.newHashSet();
        this.activeModules_ = Collections.unmodifiableSet(activeModules);
        this.activeModuleNames = Maps.newHashMap();
    }

    private static final IModuleController DEFAULT_CONTROLLER;
    private Set<IModuleContainer> registeredModules, activeModules, activeModules_;
    private Map<ResourceLocation, IModuleContainer> activeModuleNames;
    private Map<String, IModuleController> moduleControllers;
    private boolean locked;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        for (ModContainer mod : Loader.instance().getActiveModList()){
            Object modO = mod.getMod();
            if (modO instanceof IModuleController){
                IModuleController moduleController = (IModuleController) modO;
                moduleControllers.put(mod.getModId(), moduleController);
                moduleController.registerAdditionalModules(ModuleHandler.INSTANCE::registerAdditionalModule);
            }
        }
        locked = true;
        for (ASMDataTable.ASMData data : asmData.getAnnotationList(ElecModule.class)){
            IModuleController moduleController = getModuleController((String) data.getAnnotationInfo().get("owner"));


            Object oc = data.getAnnotationInfo().get("alwaysEnabled");
            boolean b = oc != null && (boolean) oc;
            boolean b1 = moduleController.isModuleEnabled((String) data.getAnnotationInfo().get("name"));


            if (b || b1/*((Boolean) data.getAnnotationInfo().get("alwaysEnabled")) || moduleController.isModuleEnabled((String) data.getAnnotationInfo().get("name"))*/){
                try {
                    Class<?> clazz = Class.forName(data.getClassName());
                    Object o = moduleController.invoke(clazz);
                    if (o == null){
                        continue;
                    }
                    IModuleContainer module = moduleController.wrap(o);
                    if (module == null){
                        continue;
                    }
                    registeredModules.add(module);
                } catch (Exception e){
                    ElecCore.logger.error("Error registering module "+data.getAnnotationInfo().get("name")+" from mod "+data.getAnnotationInfo().get("owner"));
                    ElecCore.logger.error(e);
                }
            }
        }
        Map<String, ArtifactVersion> names = Maps.newHashMap();
        for (ModContainer mod : Loader.instance().getActiveModList()){
            names.put(mod.getModId(), mod.getProcessedVersion());
        }

        List<IModuleContainer> list = Lists.newArrayList();

        for (IModuleContainer module : registeredModules){
            boolean add = true;
            Set<ArtifactVersion> missingMods = Sets.newHashSet();
            List<ArtifactVersion> requirements = module.getModDependencies();
            for (ArtifactVersion dep : requirements){
                ArtifactVersion ver = names.get(dep.getLabel());
                if (ver == null || !dep.containsVersion(ver)){
                    if (!module.autoDisableIfRequirementsNotMet()){
                        missingMods.add(dep);
                    }
                    add = false;
                }
            }
            if (!missingMods.isEmpty()){
                String s = "Module: "+module.getCombinedName().toString();
                throw new MissingModsException(missingMods, s, s);
            }
            if (add){
                list.add(module);
            }
        }
        Set<String> mNames = Sets.newHashSet();
        for (IModuleContainer module : list){
            mNames.add(module.getCombinedName().toString());
        }
        for (IModuleContainer module : list){
            boolean add = true;
            List<String> missingModules = Lists.newArrayList();
            for (String s : module.getModuleDependencies()){
                if (Strings.isNullOrEmpty(s)){
                    continue;
                }
                if (!mNames.contains(s)){
                    if (!module.autoDisableIfRequirementsNotMet()){
                        missingModules.add(s);
                    }
                    add = false;
                }
            }
            if (!missingModules.isEmpty()){
                throw new RuntimeException("Module: "+module.getCombinedName()+" requires modules(s) "+missingModules+" to be present.");
            }
            if (add){
                if (activeModuleNames.get(module.getCombinedName()) != null){
                    throw new RuntimeException("Found duplicate module name: "+module.getCombinedName());
                }
                activeModules.add(module);
                activeModuleNames.put(module.getCombinedName(), module);
            }
        }
        for (IModuleContainer module : activeModules){
            for (Field field : module.getClass().getDeclaredFields()){
                field.setAccessible(true);
                if (field.isAnnotationPresent(ElecModule.Instance.class)){
                    String name = field.getAnnotation(ElecModule.Instance.class).module();
                    if (Strings.isNullOrEmpty(name)){
                        try {
                            field.set(module.getModule(), module.getModule());
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    } else {
                        IModuleContainer module_ = activeModuleNames.get(new ResourceLocation(name));
                        try {
                            field.set(module.getModule(), module_.getModule());
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
    }

    public void registerAdditionalModule(IModuleContainer module){
        if (locked){
            throw new IllegalStateException("Mod "+Loader.instance().activeModContainer().getModId()+" attempted to register a module too late!");
        }
        if (module == null){
            return;
        }
        registeredModules.add(module);
    }

    public Set<IModuleContainer> getActiveModules(){
        return activeModules_;
    }

    public void invokeEvent(Object event){
        for (IModuleContainer module : activeModules){
            try {
                module.invokeEvent(event);
            } catch (Exception e){
                throw new RuntimeException("Error invoking event of type: " + event.getClass().getCanonicalName()+" for module: "+module.getCombinedName(), e);
            }
        }
    }

    private IModuleController getModuleController(String mod){
        IModuleController ret = moduleControllers.get(mod);
        if (ret == null){
            ElecCore.logger.error("Mod: "+mod+" does not have a module controller!");
            return DEFAULT_CONTROLLER;
        }
        return ret;
    }

    static {
        DEFAULT_CONTROLLER = new IModuleController() {

            @Override
            public boolean isModuleEnabled(String moduleName) {
                return true;
            }

        };
        ModEventHandler.registerCallback(ModuleHandler.INSTANCE::invokeEvent);
    }

}
