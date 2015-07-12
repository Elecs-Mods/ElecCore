package elec332.core.module;

import com.google.common.collect.ImmutableList;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import elec332.core.module.annotations.ElecModule;
import elec332.core.module.annotations.ModuleProxy;
import elec332.core.module.event.SetupModuleEvent;
import net.minecraftforge.common.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 9-4-2015.
 */
public class ModuleHandler {
    public ModuleHandler(Configuration config){
        this.registeredModules = new ArrayList<Object>();
        this.mainConfig = config;
    }

    private List<Object> registeredModules;
    private Configuration mainConfig;

    public void registerModule(Object object){
        registeredModules.add(object);
    }

    @SuppressWarnings("unchecked")
    public void preInit(FMLPreInitializationEvent event){
        List<Object> validModules = new ArrayList<Object>();
        for (Object object : this.registeredModules){
            Class objClass = object.getClass();
            if (objClass.isAnnotationPresent(ElecModule.class)){
                ElecModule objAnnotation = (ElecModule) objClass.getAnnotation(ElecModule.class);
                if(this.mainConfig.get("Modules", objAnnotation.name(), objAnnotation.enabled()).getBoolean()) {
                    validModules.add(object);
                    injectProxy(object, FMLCommonHandler.instance().getSide());
                }
            }
        }
        this.registeredModules.clear();
        this.registeredModules = ImmutableList.copyOf(validModules);
        SetupModuleEvent setupModuleEvent = new SetupModuleEvent(this.mainConfig);
        for (Object object : this.registeredModules){
            ElecModule moduleAnnotation = object.getClass().getAnnotation(ElecModule.class);
            invokeEvent(object, setupModuleEvent.forModule(moduleAnnotation.name()));
        }

        FMLPreInitializationEvent newEvent = new FMLPreInitializationEvent(null, event.getModConfigurationDirectory());
        for (Object object : this.registeredModules){
            ElecModule moduleAnnotation = object.getClass().getAnnotation(ElecModule.class);
            ModMetadata moduleMeta = new ModMetadata();
            moduleMeta.version = event.getModMetadata().version;
            moduleMeta.modId = moduleAnnotation.name();
            moduleMeta.name = moduleAnnotation.name();
            newEvent.applyModContainer(new DummyModContainer(moduleMeta));
            invokeEvent(object, newEvent);
        }
    }

    public void init(FMLInitializationEvent event){
        invokeForAll(event);
    }

    public void postInit(FMLPostInitializationEvent event){
        invokeForAll(event);
    }

    private void invokeForAll(Object event){
        for (Object object : this.registeredModules)
            invokeEvent(object, event);
    }

    private void injectProxy(Object module, Side side){
        for (Field field : module.getClass().getFields()){
            if (field.isAnnotationPresent(ModuleProxy.class)){
                try {
                    ModuleProxy moduleProxy = field.getAnnotation(ModuleProxy.class);
                    String className = side.isClient() ? moduleProxy.clientSide() : moduleProxy.serverSide();
                    Object newProxy = Class.forName(className).newInstance();
                    if (!field.getClass().isAssignableFrom(newProxy.getClass()))
                        continue;
                    field.set(module, newProxy);
                } catch (Throwable throwable){}
            }
        }
    }

    private void invokeEvent(Object object, Object event){
        Class objClass = object.getClass();
        for (Method method : objClass.getDeclaredMethods()){
            if (method.isAnnotationPresent(ElecModule.EventHandler.class) || method.isAnnotationPresent(Mod.EventHandler.class)){
                try {
                    if (method.getParameterTypes().length != 1)
                        continue;
                    if (!method.getParameterTypes()[0].isAssignableFrom(event.getClass()))
                        continue;
                    method.invoke(object, event);
                } catch (Throwable throwable){}
            }
        }
    }
}
