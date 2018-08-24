package elec332.core.loader;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.module.IModuleContainer;
import elec332.core.api.module.IModuleController;
import elec332.core.api.module.IModuleInfo;
import elec332.core.api.module.IModuleManager;
import elec332.core.module.DefaultWrappedModule;
import elec332.core.util.FMLUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLModContainer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 24-9-2016.
 */
enum ModuleManager implements IModuleManager {

    INSTANCE;

    ModuleManager() {
        this.registeredModules = Sets.newHashSet();
        this.moduleControllers = Maps.newHashMap();
        this.activeModules = Sets.newHashSet();
        this.activeModules_ = Collections.unmodifiableSet(activeModules);
        this.activeModuleNames = Maps.newHashMap();
        this.fieldProcessors = Maps.newHashMap();
        this.moduleDiscoverers = Sets.newHashSet();
        this.erroredMods = Sets.newHashSet();
    }

    private static final IModuleController DEFAULT_CONTROLLER;
    private static final BiFunction<Object, IModuleInfo, IModuleContainer> defaultImpl;
    private Set<IModuleContainer> activeModules, activeModules_;
    private Set<IModuleInfo> registeredModules;
    private Map<ResourceLocation, IModuleContainer> activeModuleNames;
    private Map<String, IModuleController> moduleControllers;
    private Map<Class<? extends Annotation>, Function<IModuleContainer, Object>> fieldProcessors;
    private Set<BiFunction<IASMDataHelper, Function<String, IModuleController>, List<IModuleInfo>>> moduleDiscoverers;
    private Set<String> erroredMods;
    private boolean locked, loaded;
    
    @APIHandlerInject
    private IASMDataHelper asmData = null;

    void init(){
        moduleControllers = FMLUtil.getLoader().getActiveModList().stream()
                .filter(mc -> mc.getMod() instanceof IModuleController)
                .collect(Collectors.toMap(ModContainer::getModId, mc -> (IModuleController) mc.getMod()));
        moduleControllers.values().forEach(moduleController -> moduleController.registerAdditionalModules(ModuleManager.INSTANCE::registerAdditionalModule));

        locked = true;

        moduleDiscoverers.forEach(func -> {
            List<IModuleInfo> list = func.apply(asmData, ModuleManager.this::getModuleController);
            if (list == null){
                return;
            }
            list.stream()
                    .filter(Objects::nonNull)
                    .filter(moduleInfo -> moduleInfo.alwaysEnabled() || moduleInfo.getModuleController().isModuleEnabled(moduleInfo.getName()))
                    .forEach(registeredModules::add);
        });

        List<IModuleInfo> depCheckModules = checkModDependencies();
        while (true) {
            if (depCheckModules.size() == (depCheckModules = checkModuleDependencies(depCheckModules)).size()) {
                break;
            }
        }

        constructModules(depCheckModules);

        /*activeModules.forEach(module -> {

            try {
                ((FMLEvent) event).applyModContainer(module.getOwnerMod());
                module.invokeEvent(event);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking FMLPreInitializationEvent on module " + module.getName() + ", owned by: " + module.getOwnerMod(), e.getCause());
            }

        });*/

        this.fieldProcessors.forEach(ModuleManager.INSTANCE::processModuleField);

        loaded = true;
    }

    private List<IModuleInfo> checkModDependencies(){
        Map<String, ArtifactVersion> names = Maps.newHashMap();
        for (ModContainer mod : Loader.instance().getActiveModList()) {
            names.put(mod.getModId(), mod.getProcessedVersion());
        }

        List<IModuleInfo> list = Lists.newArrayList();

        for (IModuleInfo module : registeredModules) {
            boolean add = true;
            Set<ArtifactVersion> missingMods = Sets.newHashSet();
            List<ArtifactVersion> requirements = module.getModDependencies();
            for (ArtifactVersion dep : requirements) {
                ArtifactVersion ver = names.get(dep.getLabel());
                if (ver == null || !dep.containsVersion(ver)) {
                    if (!module.autoDisableIfRequirementsNotMet()) {
                        missingMods.add(dep);
                    }
                    add = false;
                }
            }
            if (!missingMods.isEmpty()) {
                String s = "Module: " + module.getCombinedName().toString();
                throw new MissingModsException(missingMods, s, s);
            }
            if (add) {
                list.add(module);
            }
        }

        return list;
    }

    private List<IModuleInfo> checkModuleDependencies(List<IModuleInfo> list){
        Set<String> moduleNames = list.stream()
                .map(IModuleInfo::getCombinedName)
                .map(Object::toString)
                .collect(Collectors.toSet());

        return list.stream()
                .filter(moduleInfo -> {
                    List<String> missingDeps = moduleInfo.getModuleDependencies().stream()
                            .filter(Objects::nonNull)
                            .filter(s -> !moduleNames.contains(s))
                            .collect(Collectors.toList());
                    if (!missingDeps.isEmpty()){
                        if (!moduleInfo.autoDisableIfRequirementsNotMet()){
                            throw new RuntimeException("Module: " + moduleInfo.getCombinedName() + " requires module(s) " + missingDeps + " to be present.");
                        }
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private void constructModules(List<IModuleInfo> list){
        for (IModuleInfo module : list){
            if (activeModuleNames.get(module.getCombinedName()) != null) {
                throw new RuntimeException("Found duplicate module name: " + module.getCombinedName());
            }
            try {
                ModContainer owner = FMLUtil.findMod(module.getOwner());
                if (owner == null) {
                    throw new IllegalStateException("Error finding owner mod for module: " + module.getCombinedName());
                }
                IModuleContainer module_ = module.getModuleController().wrap(module, defaultImpl);
                if (module_ == null) {
                    continue;
                }
                activeModules.add(module_);
                activeModuleNames.put(new ResourceLocation(module.getCombinedName().toString().toLowerCase()), module_);
                ElecCore.logger.info("Successfully registered module " + module.getName() + " from mod " + module.getOwner());
            } catch (Exception e) {
                ElecCore.logger.error("Error registering module " + module.getName() + " from mod " + module.getOwner());
                ElecCore.logger.error(e);
            }
        }
    }

    @SuppressWarnings("all")
    private void registerModulesToModBus(){
        for (IModuleContainer module : activeModules) {
            ModContainer mc = module.getOwnerMod();
            if (!FMLUtil.hasFMLModContainer(mc)) {
                throw new UnsupportedOperationException();
            }
            try {
                ((com.google.common.eventbus.EventBus) eventBus.get(mc)).register(new Object() {

                    @com.google.common.eventbus.Subscribe
                    public void onEvent(Object event) {
                        try {
                            System.out.println("invoke "+event+" on "+module);
                            module.invokeEvent(event);
                        } catch (Exception e) {
                            throw new RuntimeException("Error invoking event on module " + module.getModule() + ", owned by: " + module.getOwnerMod(), e.getCause());
                        }
                    }

                });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void processModuleField(Class<? extends Annotation> clazz, Function<IModuleContainer, Object> function) {
        for (IModuleContainer module : activeModules) {
            for (Field field : module.getModule().getClass().getDeclaredFields()) {
                if (field.isAnnotationPresent(clazz)) {
                    field.setAccessible(true);
                    String name = "";
                    try {
                        name = (String) clazz.getDeclaredMethod("value").invoke(field.getAnnotation(clazz));
                    } catch (Exception e) {
                        //
                    }
                    IModuleContainer module_ = module;
                    if (!Strings.isNullOrEmpty(name)) {
                        module_ = activeModuleNames.get(new ResourceLocation(name));
                    }
                    Object o = function.apply(module_);
                    try {
                        field.set(module.getModule(), o);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    @SuppressWarnings("all")
    private void registerAdditionalModule(IModuleInfo module) {
        if (locked) {
            throw new IllegalStateException("Mod " + Loader.instance().activeModContainer().getModId() + " attempted to register a module too late!");
        }
        if (module == null) {
            return;
        }
        registeredModules.add(module);
    }

    @Nonnull
    @Override
    public Set<IModuleContainer> getActiveModules() {
        return activeModules_;
    }

    @Nullable
    @Override
    public IModuleContainer getActiveModule(ResourceLocation module) {
        return activeModuleNames.get(new ResourceLocation(module.toString().toLowerCase()));
    }

    @Override
    public void registerFieldProcessor(Class<? extends Annotation> annotation, Function<IModuleContainer, Object> function) {
        this.fieldProcessors.put(annotation, function);
    }

    @Override
    public void registerModuleDiscoverer(BiFunction<IASMDataHelper, Function<String, IModuleController>, List<IModuleInfo>> discoverer) {
        this.moduleDiscoverers.add(discoverer);
    }

    @Override
    public void invokeEvent(Object event) {
        if (!loaded) {
            throw new IllegalStateException();
        }
        activeModules.forEach(module -> {
            try {
                module.invokeEvent(event);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking event of type: " + event.getClass().getCanonicalName() + " for module: " + module.getCombinedName(), e);
            }
        });
    }

    @APIHandlerInject
    public void injectModuleManager(IAPIHandler apiHandler){
        apiHandler.inject(INSTANCE, IModuleManager.class);
    }

    @Nonnull
    private IModuleController getModuleController(String mod) {
        IModuleController ret = moduleControllers.get(mod);
        if (ret == null) {
            if (erroredMods.add(mod)) {
                ElecCore.logger.error("Mod: " + mod + " does not have a module controller!");
            }
            return DEFAULT_CONTROLLER;
        }
        return ret;
    }

    private static Field eventBus;

    static {
        DEFAULT_CONTROLLER = moduleName -> true;
        defaultImpl = DefaultWrappedModule::new;
        try {
            eventBus = FMLModContainer.class.getDeclaredField("eventBus");
            eventBus.setAccessible(true);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
