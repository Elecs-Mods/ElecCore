package elec332.core.loader;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.discovery.IAnnotationDataHandler;
import elec332.core.api.module.*;
import elec332.core.api.registration.APIInjectedEvent;
import elec332.core.module.DefaultWrappedModule;
import elec332.core.util.FMLHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.IModInfo;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 24-9-2016.
 */
enum ModuleManager implements IModuleManager {

    INSTANCE;

    ModuleManager() {
        this.discoveredModules = Sets.newHashSet();
        this.moduleControllers = Maps.newHashMap();
        this.constructedModules = Sets.newHashSet();
        this.activeModules = Sets.newHashSet();
        this.activeModules_ = Collections.unmodifiableSet(activeModules);
        this.activeModuleNames = Maps.newHashMap();
        this.fieldProcessors = Maps.newHashMap();
        this.moduleDiscoverers = Sets.newHashSet();
        this.erroredMods = Sets.newHashSet();
        this.checkCounter = new AtomicInteger();
        this.moduleConfig = Maps.newHashMap();
    }

    private static final IModuleController DEFAULT_CONTROLLER;
    private static final BiFunction<Object, IModuleInfo, IModuleContainer> defaultImpl;

    private Set<IModuleContainer> constructedModules, activeModules, activeModules_;
    private Set<IModuleInfo> discoveredModules;
    private Map<ResourceLocation, IModuleContainer> activeModuleNames;
    private Map<String, IModuleController> moduleControllers;
    private Map<ResourceLocation, ForgeConfigSpec.BooleanValue> moduleConfig;
    private Map<Class<? extends Annotation>, Function<IModuleContainer, Object>> fieldProcessors;
    private Set<BiFunction<IAnnotationDataHandler, Function<String, IModuleController>, List<IModuleInfo>>> moduleDiscoverers;
    private Set<String> erroredMods;
    private AtomicInteger checkCounter;
    private boolean locked, loaded;

    @APIHandlerInject
    private IAnnotationDataHandler asmData = null;

    void gatherAndConstruct() {
        moduleControllers = FMLHelper.getMods().stream()
                .filter(mc -> mc.getMod() instanceof IModuleController)
                .collect(Collectors.toMap(ModContainer::getModId, mc -> (IModuleController) mc.getMod()));
        moduleControllers.values().forEach(moduleController -> moduleController.registerAdditionalModules(ModuleManager.INSTANCE::registerAdditionalModule));

        locked = true;

        moduleDiscoverers.forEach(func -> {
            List<IModuleInfo> list = func.apply(asmData, ModuleManager.this::getModuleController);
            if (list == null) {
                return;
            }
            list.stream()
                    .filter(Objects::nonNull)
                    .forEach(discoveredModules::add);
        });

        discoveredModules.stream()
                .filter(moduleInfo -> !moduleInfo.alwaysEnabled())
                .forEach(module -> moduleConfig.put(module.getCombinedName(), module.getModuleController().getModuleConfig(module.getName())));

        List<IModuleInfo> depCheckModules = checkModDependencies();
        while (true) {
            if (depCheckModules.size() == (depCheckModules = checkModuleDependencies(depCheckModules)).size()) {
                break;
            }
        }

        constructModules(depCheckModules);

        registerModulesToModBus();

        this.fieldProcessors.forEach(ModuleManager.INSTANCE::processModuleField);

        loaded = true;
    }

    Set<IModuleContainer> getConstructedModules() {
        return constructedModules;
    }

    private List<IModuleInfo> checkModDependencies() {
        Map<String, ArtifactVersion> names = Maps.newHashMap();
        for (ModInfo modInfo : FMLHelper.getModList().getMods()) {
            names.put(modInfo.getModId(), modInfo.getVersion());
        }

        List<IModuleInfo> list = Lists.newArrayList();

        for (IModuleInfo module : discoveredModules) {
            boolean depsPresent = true;
            Set<String> missingMods = Sets.newHashSet();
            List<Pair<String, VersionRange>> requirements = module.getModDependencies();
            for (Pair<String, VersionRange> dep : requirements) {
                ArtifactVersion ver = names.get(dep.getLeft());
                if (ver == null || (dep.getRight() != IModInfo.UNBOUNDED && !dep.getRight().containsVersion(ver))) {
                    if (!module.autoDisableIfRequirementsNotMet()) {
                        missingMods.add(dep.getKey() + "@" + dep.getRight().toString());
                    }
                    depsPresent = false;
                }
            }
            if (!missingMods.isEmpty()) {
                throw new RuntimeException(String.format("Module %s is missing the required " + (missingMods.size() == 1 ? "dependency" : "dependencies") + " : {}", module.getCombinedName().toString(), missingMods));
            }
            if (module.alwaysEnabled() ? depsPresent : module.getModuleController().shouldModuleConstruct(module, depsPresent)) {
                list.add(module);
            }
        }

        return list;
    }

    private List<IModuleInfo> checkModuleDependencies(List<IModuleInfo> list) {
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
                    if (!missingDeps.isEmpty()) {
                        if (!moduleInfo.autoDisableIfRequirementsNotMet()) {
                            throw new RuntimeException("Module: " + moduleInfo.getCombinedName() + " requires module(s) " + missingDeps + " to be present.");
                        }
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    private void constructModules(List<IModuleInfo> list) {
        for (IModuleInfo module : list) {
            if (constructedModules.stream().map(IModuleInfo::getCombinedName).anyMatch(name -> name.equals(module.getCombinedName()))) {
                throw new RuntimeException("Found duplicate module name: " + module.getCombinedName());
            }
            try {
                ModContainer owner = FMLHelper.findMod(module.getOwner());
                if (owner == null) {
                    throw new IllegalStateException("Error finding owner mod for module: " + module.getCombinedName());
                }
                IModuleContainer module_ = module.getModuleController().wrap(module, defaultImpl);
                if (module_ == null) {
                    continue;
                }
                constructedModules.add(module_);
                ElecCore.logger.info("Successfully constructed module " + module.getName() + " from mod " + module.getOwner());
            } catch (Exception e) {
                ElecCore.logger.error("Error constructing module " + module.getName() + " from mod " + module.getOwner(), e);
            }
        }
    }

    private synchronized void addRegisteredModule(IModuleContainer moduleContainer) {
        activeModules.add(moduleContainer);
        activeModuleNames.put(new ResourceLocation(moduleContainer.getCombinedName().toString().toLowerCase()), moduleContainer);
    }

    private void registerModulesToModBus() {
        for (IModuleContainer module : constructedModules) {
            ModContainer mc = module.getOwnerMod();
            if (!FMLHelper.hasFMLModContainer(mc)) {
                throw new UnsupportedOperationException();
            }
            final FMLModContainer modContainer = FMLHelper.getFMLModContainer(mc);
            modContainer.getEventBus().addListener(EventPriority.LOW, (Consumer<? extends RegistryEvent<?>>) evt -> invokeEvent(module, evt));
            modContainer.getEventBus().addListener(EventPriority.LOW, (Consumer<? extends RegistryEvent.NewRegistry>) evt -> invokeEvent(module, evt));
            modContainer.getEventBus().addListener(EventPriority.LOW, (Consumer<? extends APIInjectedEvent<?>>) evt -> invokeEvent(module, evt));
            modContainer.getEventBus().addListener(EventPriority.LOW, (Consumer<FMLCommonSetupEvent>) cse -> {
                boolean add = Optional.ofNullable(moduleConfig.get(module.getCombinedName())).map(ForgeConfigSpec.BooleanValue::get).orElse(true);
                add &= module.alwaysEnabled() || module.getModuleController().isModuleEnabled(module.getName());
                if (add) {
                    addRegisteredModule(module);
                    ElecCore.logger.info("Successfully registered module " + module.getName() + " from mod " + module.getOwner());
                }
                checkCounter.incrementAndGet();
                if (add) {
                    invokeEvent(module, cse);
                    modContainer.getEventBus().addListener(EventPriority.LOW, (Consumer<ModLifecycleEvent>) event -> invokeEvent(module, event));
                    Class<?> objClass = module.getModule().getClass();
                    for (Method method : objClass.getDeclaredMethods()) {
                        if (method.isAnnotationPresent(ElecModule.EventHandler.class)/* || method.isAnnotationPresent(Mod.EventHandler.class)*/) {
                            if (method.getParameterTypes().length != 1) {
                                continue;
                            }
                            Class<?> param = method.getParameterTypes()[0];
                            if (!Event.class.isAssignableFrom(param) || ModLifecycleEvent.class.isAssignableFrom(param)) {
                                continue;
                            }
                            ElecModule.EventHandler ann = method.getAnnotation(ElecModule.EventHandler.class);
                            @SuppressWarnings("unchecked") //Checked above
                                    Class<? extends Event> type = (Class<? extends Event>) param;
                            modContainer.getEventBus().addListener(ann.priority(), ann.receiveCanceled(), type, event -> invokeEvent(module, event));
                        }
                    }
                }
            });
        }
    }

    private void invokeEvent(IModuleContainer module, Object event) {
        try {
            module.invokeEvent(event);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw new RuntimeException("Error invoking event (" + event + ") on module " + module.getModule() + ", owned by: " + module.getOwnerMod().getModId(), e.getCause());
        }
    }

    private void processModuleField(Class<? extends Annotation> clazz, Function<IModuleContainer, Object> function) {
        for (IModuleContainer module : constructedModules) {
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
                        ResourceLocation rlName = new ResourceLocation(name);
                        module_ = constructedModules.stream()
                                .filter(m -> m.getCombinedName().equals(rlName))
                                .findFirst().orElse(null);
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

    private void registerAdditionalModule(IModuleInfo module) {
        if (locked) {
            throw new IllegalStateException("Mod " + FMLHelper.getActiveModContainer() + " attempted to register a module too late!");
        }
        if (module == null) {
            return;
        }
        discoveredModules.add(module);
    }

    @Nonnull
    @Override
    public Set<IModuleContainer> getActiveModules() {
        if (checkCounter.get() != constructedModules.size()) {
            throw new UnsupportedOperationException();
        }
        return activeModules_;
    }

    @Nullable
    @Override
    public IModuleContainer getActiveModule(ResourceLocation module) {
        if (checkCounter.get() != constructedModules.size()) {
            throw new UnsupportedOperationException();
        }
        return activeModuleNames.get(new ResourceLocation(module.toString().toLowerCase()));
    }

    @Override
    public void registerFieldProcessor(Class<? extends Annotation> annotation, Function<IModuleContainer, Object> function) {
        this.fieldProcessors.put(annotation, function);
    }

    @Override
    public void registerModuleDiscoverer(BiFunction<IAnnotationDataHandler, Function<String, IModuleController>, List<IModuleInfo>> discoverer) {
        this.moduleDiscoverers.add(discoverer);
    }

    @Override
    public void invokeEvent(Object event) {
        if (!loaded) {
            throw new IllegalStateException();
        }
        getActiveModules().forEach(module -> {
            try {
                module.invokeEvent(event);
            } catch (Exception e) {
                throw new RuntimeException("Error invoking event of type: " + event.getClass().getCanonicalName() + " for module: " + module.getCombinedName(), e);
            }
        });
    }

    @APIHandlerInject
    @SuppressWarnings("unused")
    public void injectModuleManager(IAPIHandler apiHandler) {
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

    static {
        DEFAULT_CONTROLLER = moduleName -> true;
        defaultImpl = DefaultWrappedModule::new;
    }

}
