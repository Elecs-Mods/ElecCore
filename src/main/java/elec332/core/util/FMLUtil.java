package elec332.core.util;

import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLUtil {

    /**
     * Loads a class with the FML classloader
     *
     * @param clazz The class to be loaded
     * @return The loaded class
     * @throws ClassNotFoundException If the class does not exist
     */
    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz, true, FMLUtil.getLoader().getModClassLoader());
    }

    /**
     * Gets the mod that owns the class, returns "<unknown>" if the owner cannot be determined
     *
     * @param clazz The class
     * @return The name of the mod that owns the specified class
     */
    public static String getOwnerName(Class<?> clazz) {
        ModContainer mod = getOwner(clazz);
        return mod == null ? "<unknown>" : mod.getModId();
    }

    /**
     * @return The current MC version
     */
    public static String getMcVersion() {
        return (String) FMLInjectionData.data()[4];
    }

    /**
     * Gets the {@link ModContainer} that owns the class, returns null if the owner cannot be determined
     *
     * @param clazz The class
     * @return The {@link ModContainer} of the mod that owns the specified class
     */
    @Nullable
    public static ModContainer getOwner(Class<?> clazz) {
        for (ModContainer modContainer : getLoader().getActiveModList()) {
            if (modContainer.getOwnedPackages().contains(ReflectionHelper.getPackage(clazz))) {
                return modContainer;
            }
        }
        return null;
    }

    /**
     * Gets the {@link ModContainer} of a mod object, can be null if the object is invalid
     *
     * @param mod The mod object
     * @return The {@link ModContainer} of the specified mod object
     */
    @Nullable
    public static ModContainer getModContainer(Object mod) {
        return mod instanceof ModContainer ? (ModContainer) mod : FMLCommonHandler.instance().findContainerFor(mod);
    }

    /**
     * @return Whether MC is still loading
     */
    public static boolean isInModInitialisation() {
        return !hasReachedState(LoaderState.AVAILABLE);
    }

    /**
     * Checks if the {@link LoadController} has reached the specified loading state
     *
     * @param state The loading state to be checked
     * @return Whether the specified {@link LoaderState} has been reached
     */
    public static boolean hasReachedState(LoaderState state) {
        return getLoader().hasReachedState(state);
    }

    /**
     * @return The modloader
     */
    @Nonnull
    public static Loader getLoader() {
        return Loader.instance();
    }

    /**
     * Returns whether the specified {@link ModContainer} can in come way be casted to {@link FMLModContainer},
     * or if it's a custom implementation
     *
     * @param mc The modcontainer to be checked
     * @return Whether the specified {@link ModContainer} can in come way be casted to {@link FMLModContainer}
     */
    public static boolean hasFMLModContainer(ModContainer mc) {
        return mc instanceof FMLModContainer || mc instanceof InjectedModContainer && hasFMLModContainer(((InjectedModContainer) mc).wrappedContainer);
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
        if (mc instanceof InjectedModContainer) {
            return getFMLModContainer(((InjectedModContainer) mc).wrappedContainer);
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
        for (ModContainer mc : getLoader().getActiveModList()) {
            if (mc.getModId().equals(modId)) {
                return mc;
            }
        }
        return null;
    }

}
