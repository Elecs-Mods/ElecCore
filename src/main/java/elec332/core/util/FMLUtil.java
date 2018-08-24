package elec332.core.util;

import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.relauncher.FMLInjectionData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FMLUtil {

    public static Class<?> loadClass(String clazz) throws ClassNotFoundException {
        return Class.forName(clazz, true, FMLUtil.getLoader().getModClassLoader());
    }

    public static String getOwnerName(Class<?> clazz) {
        ModContainer mod = getOwner(clazz);
        return mod == null ? "<unknown>" : mod.getModId();
    }

    public static String getMcVersion() {
        return (String) FMLInjectionData.data()[4];
    }

    @Nullable
    public static ModContainer getOwner(Class<?> clazz) {
        for (ModContainer modContainer : getLoader().getActiveModList()) {
            if (modContainer.getOwnedPackages().contains(ReflectionHelper.getPackage(clazz))) {
                return modContainer;
            }
        }
        return null;
    }

    @Nullable
    public static ModContainer getModContainer(Object mod) {
        return mod instanceof ModContainer ? (ModContainer) mod : FMLCommonHandler.instance().findContainerFor(mod);
    }

    public static boolean isInModInitialisation() {
        return !hasReachedState(LoaderState.AVAILABLE);
    }

    public static boolean hasReachedState(LoaderState state) {
        return getLoader().hasReachedState(state);
    }

    @Nonnull
    public static Loader getLoader() {
        return Loader.instance();
    }

    public static boolean hasFMLModContainer(ModContainer mc) {
        return mc instanceof FMLModContainer || mc instanceof InjectedModContainer && hasFMLModContainer(((InjectedModContainer) mc).wrappedContainer);
    }

    public static FMLModContainer getFMLModContainer(ModContainer mc) {
        if (mc instanceof FMLModContainer) {
            return (FMLModContainer) mc;
        }
        if (mc instanceof InjectedModContainer) {
            return getFMLModContainer(((InjectedModContainer) mc).wrappedContainer);
        }
        throw new IllegalArgumentException();
    }

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
