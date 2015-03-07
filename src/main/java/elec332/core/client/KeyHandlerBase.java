package elec332.core.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.settings.KeyBinding;

import java.util.ArrayList;

/**
 * Created by Elec332 on 6-3-2015.
 */
public abstract class KeyHandlerBase {
    ArrayList<KeyBinding> keyBindings;
    ArrayList<String> keyBindingNames;
    protected static KeyHandlerBase KHB;

    public KeyHandlerBase() {
        keyBindings = new ArrayList<KeyBinding>();
        keyBindingNames = new ArrayList<String>();
        FMLCommonHandler.instance().bus().register(this);
        KHB = this;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void keyEvent(InputEvent.KeyInputEvent event) {
        for (KeyBinding key : keyBindings) {
            if (key.getIsKeyPressed() && FMLClientHandler.instance().getClient().inGameHasFocus) {
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
