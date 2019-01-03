package elec332.core.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import elec332.core.ElecCore;
import elec332.core.loader.ElecCoreLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.event.ModLifecycleEvent;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.javafmlmod.FMLModLoadingContext;
import net.minecraftforge.fml.language.ModFileScanData;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLHelper {

    private static FieldPointer<ModFileScanData.ClassData, Type> classOwner = new FieldPointer<>(ModFileScanData.ClassData.class, "clazz");
    private static BiMap<ModLoadingStage, Class<? extends ModLifecycleEvent>> eventMap;
    private static Map<String, ModContainer> mods = null;
    private static boolean mlVerify = false;

    private static Map<String, ModContainer> buildModList() {
        return Collections.unmodifiableMap(
                getModList().getMods().stream()
                        .map(mi -> getModList().getModContainerById(mi.getModId()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(ModContainer::getModId, m -> m))
        );
    }

    /**
     * Loads a class with the FML classloader
     *
     * @param clazz The class to be loaded
     * @return The loaded class
     * @throws ClassNotFoundException If the class does not exist
     */
    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz, true, FMLLoader.getLaunchClassLoader());
    }

    /**
     * Gets the mod that owns the class, returns "<unknown>" if the owner cannot be determined
     *
     * @param clazz The class
     * @return The name of the mod that owns the specified class
     */
    public static String getOwnerName(Class<?> clazz) {
        String cn = Type.getType(clazz).getClassName();
        return getModList().getMods()
                .stream()
                .filter(mi -> mi.getOwningFile().getFile().getScanResult().getClasses()
                        .stream()
                        .anyMatch(cd -> classOwner.get(cd).getClassName().equals(cn)))
                .findFirst() //Cannot account for multiple mods in 1 jar (yet...)
                .map(ModInfo::getModId)
                .orElse("<unknown>");
    }

    /**
     * Used to get the {@link ModLoadingStage} belonging to the provided {@link ModLifecycleEvent}
     *
     * @param event The event to get the {@link ModLoadingStage} from
     * @return The {@link ModLoadingStage} belonging to the provided {@link ModLifecycleEvent}
     */
    public static ModLoadingStage getStageFrom(ModLifecycleEvent event) {
        System.out.println("event to stage: " + eventMap.inverse().get(event.getClass()));
        return eventMap.inverse().get(event.getClass());
    }

    /**
     * @return The current MC version
     */
    public static String getMcVersion() {
        return DefaultModContainers.MINECRAFT.getModInfo().getVersion().toString();//(String) FMLInjectionData.data()[4];
    }

    /**
     * @return The distribution, client or dedicated servert
     */
    public static Dist getDist() {
        return FMLLoader.getDist();
    }

    /**
     * @return The effective side
     */
    public static LogicalSide getLogicalSide() {
        return EffectiveSide.get();
    }

    /**
     * @return The {@link ModContainer} currently active
     */
    public static FMLModContainer getActiveModContainer() {
        return getModContext().getActiveContainer();
    }

    /**
     * Gets the {@link ModContainer} that owns the class, returns null if the owner cannot be determined
     *
     * @param clazz The class
     * @return The {@link ModContainer} of the mod that owns the specified class
     */
    @Nullable
    public static ModContainer getOwner(Class<?> clazz) {
        return findMod(getOwnerName(clazz));
    }

    /**
     * Gets the {@link ModContainer} of a mod object, can be null if the object is invalid
     *
     * @param mod The mod object
     * @return The {@link ModContainer} of the specified mod object
     */
    @Nullable
    public static ModContainer getModContainer(Object mod) {
        return mod instanceof ModContainer ? (ModContainer) mod : ModList.get().getModContainerByObject(mod).orElse(null);
    }

    /**
     * @return Whether MC is still loading
     */
    public static boolean isInModInitialisation() {
        return !hasReachedState(ModLoadingStage.COMPLETE);
    }

    /**
     * Checks if the {@link ModLoader} has reached the specified loading state
     *
     * @param state The loading state to be checked
     * @return Whether the specified {@link ModLoadingStage} has been reached
     */
    public static boolean hasReachedState(ModLoadingStage state) {
        return ElecCoreLoader.getLastStage().ordinal() >= (state.ordinal() - 1);
    }

    /**
     * @return The modloader
     */
    @Nonnull
    public static ModLoader getLoader() {
        return ModLoader.get();
    }

    /**
     * @return The modlist
     */
    public static ModList getModList() {
        return ModList.get();
    }

    /**
     * @return All currently loaded mods
     */
    public static Collection<ModContainer> getMods() {
        if (mods == null) {
            mods = buildModList(); // Calling this in the static initializer causes an error in building the FMLModContainer's
        }
        if (!mlVerify) {
            if (!mods.keySet().contains(ElecCore.MODID)) {
                mods = buildModList();
            } else {
                mlVerify = true;
            }
        }
        return mods.values();
    }

    /**
     * Checks if the provided mod-id is loaded
     *
     * @param modid The mod-id
     * @return Whether the provided mod-id is loaded
     */
    public static boolean isModLoaded(String modid) {
        return getModList().getModContainerById(modid).isPresent();
    }

    /**
     * Used to do work after a certain {@link ModLifecycleEvent}, as mods are loaded asynchronously
     *
     * @param workToEnqueue The work to be done after the event
     */
    public static void enqueueWorkAfterEvent(Runnable workToEnqueue) {
        DeferredWorkQueue.enqueueWork((Callable<Void>) () -> {
            workToEnqueue.run();
            return null;
        });
    }

    /**
     * Used to do work after a certain {@link ModLifecycleEvent}, as mods are loaded asynchronously
     *
     * @param workToEnqueue The work to be done after the event
     * @return The future of the callable
     */
    public static <T> Future<T> enqueueWorkAfterEvent(Callable<T> workToEnqueue) {
        return DeferredWorkQueue.enqueueWork(workToEnqueue);
    }

    /**
     * @return The {@link FMLModLoadingContext}, with data on the mod being handled
     */
    public static FMLModLoadingContext getModContext() {
        return FMLModLoadingContext.get();
    }

    /**
     * Returns whether the specified {@link ModContainer} can in come way be casted to {@link FMLModContainer},
     * or if it's a custom implementation
     *
     * @param mc The modcontainer to be checked
     * @return Whether the specified {@link ModContainer} can in come way be casted to {@link FMLModContainer}
     */
    public static boolean hasFMLModContainer(ModContainer mc) {
        return mc instanceof FMLModContainer;
    }

    /**
     * Returns the {@link FMLModContainer} of this {@link ModContainer}
     * Throws an {@link IllegalArgumentException} if the specified {@link ModContainer} cannot be cast into an {@link FMLModContainer}
     *
     * @param mc The modcontainer to be checked
     * @return Whether the {@link FMLModContainer} of the specified modcontainer
     */
    public static FMLModContainer getFMLModContainer(ModContainer mc) {
        if (mc instanceof FMLModContainer) {
            return (FMLModContainer) mc;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Finds a {@link ModContainer} by it's mod-id
     *
     * @param modId The mod-id
     * @return The {@link ModContainer}
     */
    @Nullable
    public static ModContainer findMod(String modId) {
        return getModList().getModContainerById(modId).orElse(null);
    }

    static {
        eventMap = HashBiMap.create();
        Arrays.stream(ModLoadingStage.values()).forEach(new Consumer<ModLoadingStage>() {

            @Override
            public void accept(ModLoadingStage modLoadingStage) {
                Class<? extends ModLifecycleEvent> clazz = null;
                try {
                    clazz = modLoadingStage.getModEvent(null).getClass();
                    eventMap.put(modLoadingStage, clazz);
                } catch (Exception e) {
                    //nbc
                }
            }
        });
    }

}
