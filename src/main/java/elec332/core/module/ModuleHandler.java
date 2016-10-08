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
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
        this.erroredMods = Sets.newHashSet();
    }

    private static final IModuleController DEFAULT_CONTROLLER;
    private Set<IModuleContainer> activeModules, activeModules_;
    private Set<IModuleInfo> registeredModules;
    private Map<ResourceLocation, IModuleContainer> activeModuleNames;
    private Map<String, IModuleController> moduleControllers;
    private Set<String> erroredMods;
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
            try {
                IModuleInfo module = moduleController.getModuleInfo(data);
                if (module == null){
                    continue;
                }
                if (module.alwaysEnabled() || moduleController.isModuleEnabled(module.getName())){
                    registeredModules.add(module);
                }
            } catch (Exception e) {
                ElecCore.logger.error("Error fetching information for module " + data.getAnnotationInfo().get("name") + " from mod " + data.getAnnotationInfo().get("owner"));
                ElecCore.logger.error(e);
            }
        }
        Map<String, ArtifactVersion> names = Maps.newHashMap();
        for (ModContainer mod : Loader.instance().getActiveModList()){
            names.put(mod.getModId(), mod.getProcessedVersion());
        }

        List<IModuleInfo> list = Lists.newArrayList();

        for (IModuleInfo module : registeredModules){
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
        for (IModuleInfo module : list){
            mNames.add(module.getCombinedName().toString());
        }
        for (IModuleInfo module : list){
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
                throw new RuntimeException("Module: "+module.getCombinedName()+" requires module(s) "+missingModules+" to be present.");
            }
            if (add){
                if (activeModuleNames.get(module.getCombinedName()) != null){
                    throw new RuntimeException("Found duplicate module name: "+module.getCombinedName());
                }
                try {
                    IModuleContainer module_ = module.getModuleController().wrap(module);
                    if (module_ == null){
                        continue;
                    }
                    activeModules.add(module_);
                    activeModuleNames.put(module.getCombinedName(), module_);
                    ElecCore.logger.info("Successfully registered module " + module.getName() + " from mod " + module.getOwner());
                } catch (Exception e) {
                    ElecCore.logger.error("Error registering module " + module.getName() + " from mod " + module.getOwner());
                    ElecCore.logger.error(e);
                }
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

    public void registerAdditionalModule(IModuleInfo module){
        if (locked){
            throw new IllegalStateException("Mod "+Loader.instance().activeModContainer().getModId()+" attempted to register a module too late!");
        }
        if (module == null){
            return;
        }
        registeredModules.add(module);
    }

    @Nonnull
    public Set<IModuleContainer> getActiveModules(){
        return activeModules_;
    }

    @Nullable
    public IModuleContainer getActiveModule(ResourceLocation module){
        return activeModuleNames.get(module);
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

    @Nonnull
    private IModuleController getModuleController(String mod){
        IModuleController ret = moduleControllers.get(mod);
        if (ret == null){
            if (erroredMods.add(mod)) {
                ElecCore.logger.error("Mod: " + mod + " does not have a module controller!");
            }
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
