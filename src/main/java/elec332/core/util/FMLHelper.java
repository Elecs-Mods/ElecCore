package elec332.core.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import elec332.core.ElecCore;
import elec332.core.loader.ElecCoreLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.*;
import net.minecraftforge.fml.common.thread.EffectiveSide;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import net.minecraftforge.forgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLHelper {

    private static final FieldPointer<ModFileScanData.ClassData, Type> classOwner = new FieldPointer<>(ModFileScanData.ClassData.class, "clazz");
    private static final BiMap<ModLoadingStage, Class<? extends ModLifecycleEvent>> eventMap;
    private static Map<String, ModContainer> mods = null;
    private static final Map<ModLoadingStage, String> modLoadingNames;
    private static boolean mlVerify = false;

    private static Map<String, ModContainer> buildModList() {
        return Collections.unmodifiableMap(
                getModList().getMods().stream()
                        .map(mi -> getModList().getModContainerById(mi.getModId()).orElse(null))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toMap(ModContainer::getModId, m -> m))
        );
    }

    @Nonnull
    public static ModContainer getMinecraftModContainer() {
        return getModList().getModContainerById("minecraft").orElseThrow(IllegalStateException::new);
    }

    /**
     * Loads a class with the FML classloader
     *
     * @param clazz The class to be loaded
     * @return The loaded class
     * @throws ClassNotFoundException If the class does not exist
     */
    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz, true, Thread.currentThread().getContextClassLoader());//, true, FMLLoader.getLaunchClassLoader());
    }

    /**
     * Gets the mod that owns the class, returns "<unknown>" if the owner cannot be determined
     *
     * @param clazz The class
     * @return The name of the mod that owns the specified class
     */
    public static String getOwnerName(Class<?> clazz) {
        String cn = Type.getType(clazz).getClassName();
        if (cn.startsWith("net.minecraft.")) {
            return getMinecraftModContainer().getModId();
        }
        return getModList().getMods()
                .stream()
                //todo: check with 1.14
                //.filter(mi -> mi != DefaultModInfos.minecraftModInfo) //The minecraft mod-info has a null file
                .filter(mi -> mi.getOwningFile().getFile().getScanResult().getClasses().stream()
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
        return eventMap.inverse().get(event.getClass());
    }

    /**
     * Used to get the {@link ModLifecycleEvent} belonging to the provided {@link ModLoadingStage}
     *
     * @param stage The stage to get the {@link ModLifecycleEvent} type from
     * @return The {@link ModLifecycleEvent} type belonging to the provided {@link ModLoadingStage}
     */
    public static Class<? extends ModLifecycleEvent> getEventClass(ModLoadingStage stage) {
        if (!eventMap.containsKey(stage)) {
            throw new IllegalArgumentException();
        }
        return eventMap.get(stage);
    }

    /**
     * Returns a more user-friendly name for the provided mod loading stage
     *
     * @param stage The {@link ModLoadingStage} to get a name from
     * @return A name for the provided {@link ModLoadingStage}
     */
    public static String getLoadingStageName(ModLoadingStage stage) {
        return modLoadingNames.get(stage);
    }

    /**
     * @return The current MC version
     */
    public static String getMcVersion() {
        return getMinecraftModContainer().getModInfo().getVersion().toString();//(String) FMLInjectionData.data()[4];
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
     * @return The {@link IEventBus} from the mod currently active
     */
    public static IEventBus getActiveModEventBus() {
        Object o = getModLoadingContext().extension();
        if (o instanceof FMLJavaModLoadingContext) {
            return ((FMLJavaModLoadingContext) o).getModEventBus();
        }
        throw new UnsupportedOperationException();
    }

    /**
     * @return The {@link ModContainer} currently active
     */
    public static ModContainer getActiveModContainer() {
        return getModLoadingContext().getActiveContainer();
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
        ModLoadingStage stage = ElecCoreLoader.getLastStage();
        if (stage == null) {
            stage = ModLoadingStage.values()[ModLoadingStage.CONSTRUCT.ordinal() - 1];
        }
        return stage.ordinal() >= (state.ordinal() - 1);
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
     * Helper method for getting the name of a mod before the modloader has initialized
     *
     * @param modId The modid to fetch the name from
     * @return The modname for the provided modid
     */
    public static String getModNameEarly(String modId) {
        //Jap, this works...
        return FMLHelper.getModList().getMods().stream().filter(mi -> mi.getModId().equals(modId)).findFirst().orElseThrow(RuntimeException::new).getDisplayName();
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
    public static void runLater(Runnable workToEnqueue) {
        DeferredWorkQueue.runLater(workToEnqueue);
    }

    /**
     * Used to do work after a certain {@link ModLifecycleEvent}, as mods are loaded asynchronously
     *
     * @param workToEnqueue The work to be done after the event
     * @return The future of the callable
     */
    @Nonnull
    public static <T> Supplier<T> getLater(Callable<T> workToEnqueue) {
        Future<T> f = DeferredWorkQueue.getLaterChecked(workToEnqueue);
        return () -> {
            try {
                return f.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e.getCause());
            }
        };
    }

    /**
     * @return The {@link ModLoadingContext}, with data on the mod being handled
     */
    public static ModLoadingContext getModLoadingContext() {
        return ModLoadingContext.get();
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
        modLoadingNames = Maps.newEnumMap(ModLoadingStage.class);
        for (ModLoadingStage stage : ModLoadingStage.values()) {
            String name;
            switch (stage) {
                case COMMON_SETUP:
                    name = "Pre-Initialized";
                    break;
                case ENQUEUE_IMC:
                    name = "Initialized";
                    break;
                case PROCESS_IMC:
                    name = "Post-Initialized";
                    break;
                case LOAD_REGISTRIES:
                    name = "Registered objects";
                    break;
                case COMPLETE:
                    name = "Finished loading";
                    break;
                case SIDED_SETUP:
                    name = "Sided-Setup";
                default:
                    name = stage.toString();
            }
            modLoadingNames.put(stage, name);
        }
    }

}
