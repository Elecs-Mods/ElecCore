package elec332.core.module;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.module.annotations.ElecModule;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 24-9-2016.
 */
public class ModuleHandler2 {

    public ModuleHandler2(){
        this.registeredModules = Lists.newArrayList();
    }

    private List<Object> registeredModules;

    public void registerModule(Object object){
        registeredModules.add(object);
    }

    @SuppressWarnings("unchecked")
    public void preInit(FMLPreInitializationEvent event){
        List<Object> validModules = new ArrayList<Object>();

        this.registeredModules.clear();
        this.registeredModules = ImmutableList.copyOf(validModules);
        //SetupModuleEvent setupModuleEvent = new SetupModuleEvent();
        for (Object object : this.registeredModules){
            ElecModule moduleAnnotation = object.getClass().getAnnotation(ElecModule.class);
            //invokeEvent(object, setupModuleEvent.forModule(moduleAnnotation.name()));
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
                } catch (Throwable throwable){
                    //
                }
            }
        }
    }

}
