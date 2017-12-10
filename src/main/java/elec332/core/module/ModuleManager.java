package elec332.core.module;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.Subscribe;
import elec332.core.api.discovery.ASMDataProcessor;
import elec332.core.api.discovery.IASMDataHelper;
import elec332.core.api.discovery.IASMDataProcessor;
import elec332.core.api.module.*;
import elec332.core.main.APIHandler;
import elec332.core.main.ElecCore;
import elec332.core.util.FMLUtil;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;
import net.minecraftforge.fml.common.MissingModsException;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by Elec332 on 24-9-2016.
 */
@APIHandler.StaticLoad
@ASMDataProcessor(LoaderState.PREINITIALIZATION)
public enum ModuleManager implements IASMDataProcessor, IModuleManager {

    INSTANCE;

    ModuleManager(){
        this.registeredModules = Sets.newHashSet();
        this.moduleControllers = Maps.newHashMap();
        this.activeModules = Sets.newHashSet();
        this.activeModules_ = Collections.unmodifiableSet(activeModules);
        this.activeModuleNames = Maps.newHashMap();
        this.erroredMods = Sets.newHashSet();
    }

    private static final IModuleController DEFAULT_CONTROLLER;
    private static final IDefaultModuleImplementations defaultImpl;
    private Set<IModuleContainer> activeModules, activeModules_;
    private Set<IModuleInfo> registeredModules;
    private Map<ResourceLocation, IModuleContainer> activeModuleNames;
    private Map<String, IModuleController> moduleControllers;
    private Set<String> erroredMods;
    private boolean locked, loaded;
    private IASMDataHelper asmData;

    @Override
    public void processASMData(IASMDataHelper asmData, LoaderState state) {
        this.asmData = asmData;
    }

    @Subscribe
    public void preInitLast(Object event){
        if (event.getClass() != FMLPreInitializationEvent.class || loaded){
            return;
        }
        for (ModContainer mod : Loader.instance().getActiveModList()){
            Object modO = mod.getMod();
            if (modO instanceof IModuleController){
                IModuleController moduleController = (IModuleController) modO;
                moduleControllers.put(mod.getModId(), moduleController);
                moduleController.registerAdditionalModules(ModuleManager.INSTANCE::registerAdditionalModule);
            }
        }
        locked = true;
        for (ASMDataTable.ASMData data : asmData.getAnnotationList(ElecModule.class)){
            IModuleController moduleController = getModuleController((String) data.getAnnotationInfo().get("owner"));
            try {
                IModuleInfo module = moduleController.getModuleInfo(data, defaultImpl);
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
        ModContainer activeContainer = FMLUtil.getLoader().activeModContainer();
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
                    ModContainer owner = FMLUtil.findMod(module.getOwner());
                    if (owner == null){
                        throw new IllegalStateException("Error finding owner mod for module: "+module.getCombinedName());
                    }
                    FMLUtil.setActiveContainer(owner);
                    IModuleContainer module_ = module.getModuleController().wrap(module, defaultImpl);
                    FMLUtil.setActiveContainer(activeContainer);
                    if (module_ == null){
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
        for (IModuleContainer module : activeModules){
            ModContainer mc = module.getOwnerMod();
            if (!FMLUtil.hasFMLModContainer(mc)){
                throw new UnsupportedOperationException();
            }
            FMLUtil.registerToModBus(FMLUtil.getFMLModContainer(mc), new Object(){

                @Subscribe
                public void onEvent(Object event){
                    try {
                        module.invokeEvent(event);
                    } catch (Exception e){
                        throw new RuntimeException("Error invoking event on module "+module.getModule()+", owned by: "+module.getOwnerMod(), e.getCause());
                    }
                }

            });
        }
        forEachModule(module -> {

            try {
                ((FMLEvent) event).applyModContainer(module.getOwnerMod());
                module.invokeEvent(event);
            } catch (Exception e){
                throw new RuntimeException("Error invoking FMLPreInitializationEvent on module "+module.getName()+", owned by: "+module.getOwnerMod(), e.getCause());
            }

        });
        processModuleField(ElecModule.Instance.class, IModuleContainer::getModule);
        processModuleField(ElecModule.Network.class, new Function<IModuleContainer, Object>() {

            @Override
            public Object apply(IModuleContainer iModuleContainer) {
                return iModuleContainer.getHandler().getPacketHandler();
            }

        });

        loaded = true;
    }

    private void processModuleField(Class<? extends Annotation> clazz, Function<IModuleContainer, Object> function){
        for (IModuleContainer module : activeModules){
            for (Field field : module.getModule().getClass().getDeclaredFields()){
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
        return activeModuleNames.get(new ResourceLocation(module.toString().toLowerCase()));
    }

    //Correctly blames the owner mods upon a crash instead of ElecCore
    private void forEachModule(Consumer<IModuleContainer> consumer){
        ModContainer mc = FMLUtil.getLoader().activeModContainer();
        for (IModuleContainer module : activeModules){
            FMLUtil.setActiveContainer(module.getOwnerMod());
            consumer.accept(module);
        }
        FMLUtil.setActiveContainer(mc);
    }

    public void invokeEvent(Object event){
        if (!loaded){
            throw new IllegalStateException();
        }
        forEachModule(new Consumer<IModuleContainer>() {

            @Override
            public void accept(IModuleContainer module) {
                try {
                    module.invokeEvent(event);
                } catch (Exception e){
                    throw new RuntimeException("Error invoking event of type: " + event.getClass().getCanonicalName()+" for module: "+module.getCombinedName(), e);
                }
            }

        });
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
        defaultImpl = new IDefaultModuleImplementations() {

            @Override
            public IModuleInfo newDefaultModuleInfo(String owner, String name, String modDependencies, String moduleDependencies, boolean autoDisableIfRequirementsNotMet, boolean alwaysOn, String mainClazz, IModuleController moduleController) {
                return new DefaultModuleInfo(owner, name, modDependencies, moduleDependencies, autoDisableIfRequirementsNotMet, alwaysOn, mainClazz, moduleController);
            }

            @Override
            public IModuleContainer newDefaultModuleContainer(Object moduleInstance, IModuleInfo moduleInfo) {
                return new DefaultWrappedModule(moduleInstance, moduleInfo);
            }

        };
        APIHandler.INSTANCE.inject(INSTANCE, IModuleManager.class);
    }

}
