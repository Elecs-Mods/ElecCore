package elec332.core.client;

import net.minecraft.client.GameSettings;
import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.resources.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 23-12-2019
 */
@OnlyIn(Dist.CLIENT)
public class ClientHelper {

    public static void addResourcePack(IResourcePack resourcePack) {
        addResourcePack(getMinecraft().getResourceManager(), resourcePack);
    }

    public static void addResourcePack(IResourceManager resourceManager, IResourcePack resourcePack) {
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
    }

}
