package elec332.core.client;

import net.minecraft.client.KeyboardListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Elec332 on 23-12-2019
 */
@OnlyIn(Dist.CLIENT)
public class ClientHelper {

    private static final Minecraft mc;

    public static Minecraft getMinecraft() {
        return mc;
    }

    public static KeyboardListener getKeyboardListener() {
        return getMinecraft().keyboardListener;
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

    static {
        mc = Minecraft.getInstance();
    }

}
