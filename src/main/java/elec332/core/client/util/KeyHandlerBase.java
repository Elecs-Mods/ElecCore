package elec332.core.client.util;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

/**
 * Created by Elec332 on 6-3-2015.
 */
@SideOnly(Side.CLIENT)
@SuppressWarnings({"deprecation", "unused"})
@Deprecated
public abstract class KeyHandlerBase {

    ArrayList<KeyBinding> keyBindings;
    ArrayList<String> keyBindingNames;
    protected static KeyHandlerBase KHB;

    public KeyHandlerBase() {
        keyBindings = new ArrayList<KeyBinding>();
        keyBindingNames = new ArrayList<String>();
        MinecraftForge.EVENT_BUS.register(this);
        KHB = this;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        for (KeyBinding key : keyBindings) {
            if (key.isPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
                performAction(key);
            }
        }
    }

    public void registerKeyBinding(KeyBinding keyBinding){
        ClientRegistry.registerKeyBinding(keyBinding);
        keyBindings.add(keyBinding);
        keyBindingNames.add(keyBinding.getKeyDescription());
    }

    public void registerKeyBinding(String buttonName, int KeyBoardKey, String unlocalisedGroupName){
        KeyBinding keyBinding = new KeyBinding(buttonName, KeyBoardKey, unlocalisedGroupName);
        ClientRegistry.registerKeyBinding(keyBinding);
        keyBindings.add(keyBinding);
        keyBindingNames.add(keyBinding.getKeyDescription());
    }

    public KeyBinding getKeyBinding(String s){
        if (keyBindingNames.contains(s)){
            return keyBindings.get(keyBindingNames.indexOf(s));
        }
        return null;
    }

    public abstract void performAction(KeyBinding key);

}
