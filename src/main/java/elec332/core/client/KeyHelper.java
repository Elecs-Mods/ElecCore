package elec332.core.client;

import org.lwjgl.input.Keyboard;

/**
 * Created by Elec332 on 24-3-2015.
 */
public class KeyHelper {
    public static boolean isShiftDown(){
        return Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
    }
}
