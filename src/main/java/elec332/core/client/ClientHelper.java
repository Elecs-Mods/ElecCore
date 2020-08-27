package elec332.core.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.util.ISimpleResourcePack;
import elec332.core.client.util.InternalResourcePack;
import elec332.core.util.FMLHelper;
import joptsimple.internal.Strings;
import net.minecraft.client.GameSettings;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;

import javax.annotation.Nonnull;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 23-12-2019
 */
@StaticLoad
@OnlyIn(Dist.CLIENT)
public class ClientHelper {

    private static final Multimap<String, ISimpleResourcePack> resourcePacks = HashMultimap.create();

    public static void addResourcePack(ISimpleResourcePack resourcePack, String... domains) {
        if (domains == null || domains.length < 1) {
            throw new IllegalArgumentException("No domains specified"); //Better to throw an exception than to chase bugs because of a missing entry
        }
        for (String s : domains) {
            resourcePacks.put(s, resourcePack);
        }
    }

    public static void addResourcePack(final IResourceManager resourceManager, final IResourcePack resourcePack) {
        if (resourceManager instanceof SimpleReloadableResourceManager) {
            ((SimpleReloadableResourceManager) resourceManager).addResourcePack(resourcePack);
        } else if (resourceManager instanceof FallbackResourceManager) {
            ((FallbackResourceManager) resourceManager).addResourcePack(resourcePack);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static KeyboardListener getKeyboardListener() {
        return getMinecraft().keyboardListener;
    }

    public static GameSettings getGameSettings() {
        return getMinecraft().gameSettings;
    }

    public static void registerReloadListener(IFutureReloadListener reloadListener) {
        if (!(getResourceManager() instanceof IReloadableResourceManager)) {
            throw new IllegalStateException();
        }
        ((IReloadableResourceManager) getResourceManager()).addReloadListener(reloadListener);
    }

    public static IResourceManager getResourceManager() {
        return getMinecraft().getResourceManager();
    }

    public static boolean isShiftKeyDown() {
        return Screen.hasShiftDown();
    }

    public static boolean isCtrlDown() {
        return Screen.hasControlDown();
    }

    public static boolean isAltDown() {
        return Screen.hasAltDown();
    }

    private static final Minecraft mc;

    static {
        mc = Minecraft.getInstance();
        addResourcePack(mc.getResourceManager(), new InternalResourcePack("thread_safe_resources", FMLHelper.getModList().getMods().stream().map(ModInfo::getModId).collect(Collectors.toSet())) {

            @Nonnull
            @Override
            public synchronized InputStream getResourceStream(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location) {
                String nameSpace = location.getNamespace();
                if (!Strings.isNullOrEmpty(nameSpace)) {
                    return resourcePacks.get(nameSpace).stream()
                            .filter(srp -> srp.resourceExists(type, location))
                            .findFirst()
                            .map(srp -> srp.getResourceStream(type, location))
                            .orElseThrow(NoSuchElementException::new);
                }
                throw new RuntimeException();
            }

            @Override
            public synchronized boolean resourceExists(@Nonnull ResourcePackType type, @Nonnull ResourceLocation location) {
                String nameSpace = location.getNamespace();
                if (!Strings.isNullOrEmpty(nameSpace)) {
                    return resourcePacks.get(nameSpace).stream().anyMatch(srp -> srp.resourceExists(type, location));
                }
                return false;
            }

        });
    }

}
