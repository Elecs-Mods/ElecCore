package elec332.core.util;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLUtil {

    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz, true, FMLUtil.getLoader().getModClassLoader());
    }

    public static String getOwnerName(Class<?> clazz){
        ModContainer mod = getOwner(clazz);
        return mod == null ? "<unknown>" : mod.getModId();
    }

    public static String getMcVersion(){
        return (String) FMLInjectionData.data()[4];
    }

    @Nullable
    public static ModContainer getOwner(Class<?> clazz){
        for (ModContainer modContainer : getLoader().getActiveModList()){
            if (modContainer.getOwnedPackages().contains(clazz.getPackage().getName())){
                return modContainer;
            }
        }
        return null;
    }

    @Nullable
    public static ModContainer getModContainer(Object mod){
        return mod instanceof ModContainer ? (ModContainer) mod : FMLCommonHandler.instance().findContainerFor(mod);
    }

    public static boolean isInModInitialisation(){
        return !hasReachedState(LoaderState.AVAILABLE);
    }

    public static boolean hasReachedState(LoaderState state){
        return getLoader().hasReachedState(state);
    }

    @Nonnull
    public static Loader getLoader(){
        return Loader.instance();
    }

    @Nonnull
    public static ASMDataTable getASMDataTable(){
        if (dataTable == null){
            try {
                Field f = ModAPIManager.class.getDeclaredField("dataTable");
                f.setAccessible(true);
                dataTable = Preconditions.checkNotNull((ASMDataTable) f.get(ModAPIManager.INSTANCE));
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return dataTable;
    }

    public static boolean hasFMLModContainer(ModContainer mc) {
        return mc instanceof FMLModContainer || mc instanceof InjectedModContainer && hasFMLModContainer(getWrappedContainer((InjectedModContainer) mc));
    }

    public static FMLModContainer getFMLModContainer(ModContainer mc){
        if (mc instanceof FMLModContainer){
            return (FMLModContainer) mc;
        }
        if (mc instanceof InjectedModContainer){
            return getFMLModContainer(getWrappedContainer((InjectedModContainer) mc));
        }
        throw new IllegalArgumentException();
    }

    public static ModContainer getWrappedContainer(InjectedModContainer mc){
        try {
            return (ModContainer) wrappedContainer.get(mc);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static void registerToModBus(FMLModContainer modContainer, Object o){
        try {
            ((EventBus)eventBus.get(modContainer)).register(o);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isModEnabled(FMLModContainer mc){
        try {
            return mcEnabled.getBoolean(mc);
        } catch (Exception e){
            throw new RuntimeException();
        }
    }

    public static EventBus getMainModBus(){
        try {
            return (EventBus) mainModBus.get(getLoadController());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public static LoadController getLoadController(){
        if (lc == null){
            try {
                Field f = Loader.class.getDeclaredField("modController");
                f.setAccessible(true);
                lc = Preconditions.checkNotNull((LoadController) f.get(getLoader()));
            } catch (Exception e){
                throw new RuntimeException(e);
            }
        }
        return lc;
    }

    @Nullable
    public static ModContainer findMod(String modId){
        for (ModContainer mc : getLoader().getActiveModList()){
            if (mc.getModId().equals(modId)){
                return mc;
            }
        }
        return null;
    }

    public static void runAs(ModContainer as, Runnable runnable){
        ModContainer mc = FMLUtil.getLoadController().activeContainer();
        FMLUtil.setActiveContainer(as);
        runnable.run();
        FMLUtil.setActiveContainer(mc);
    }

    //Sad hack, only used when invoking events on modules
    public static void setActiveContainer(ModContainer mc){
        try {
            mcForce.invoke(FMLUtil.getLoadController(), mc);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    private static ASMDataTable dataTable;
    private static LoadController lc;
    private static final Field eventMethods, eventBus, mainModBus, mcEnabled, wrappedContainer;
    private static final Method mcForce;

    static {
        try {
            eventMethods = FMLModContainer.class.getDeclaredField("eventMethods");
            eventMethods.setAccessible(true);
            eventBus = FMLModContainer.class.getDeclaredField("eventBus");
            eventBus.setAccessible(true);
            mainModBus = LoadController.class.getDeclaredField("masterChannel");
            mainModBus.setAccessible(true);
            mcEnabled = FMLModContainer.class.getDeclaredField("enabled");
            mcEnabled.setAccessible(true);
            wrappedContainer = InjectedModContainer.class.getDeclaredField("wrappedContainer");
            wrappedContainer.setAccessible(true);
            mcForce = LoadController.class.getDeclaredMethod("forceActiveContainer", ModContainer.class);
            mcForce.setAccessible(true);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

}
