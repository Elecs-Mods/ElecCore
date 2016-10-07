package elec332.core.handler;

import com.google.common.collect.Sets;
import elec332.core.api.annotations.CallbackProcessor;
import elec332.core.api.util.ICallbackProcessor;
import elec332.core.main.ElecCore;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

import java.util.List;
import java.util.Set;

/**
 * Created by Elec332 on 29-9-2016.
 */
@CallbackProcessor
public final class ModEventHandler implements ICallbackProcessor {

    private ModEventHandler(){
    }

    private static Set<Callback> callbacks;
    private static boolean locked = false;

    public static boolean registerCallback(Callback callback) {
        return !locked && callbacks.add(callback);
    }

    @Override
    public void getCallbacks(List<?> callbacks) {
        for (Object o : callbacks){
            if (o instanceof Callback){
                ModEventHandler.callbacks.add((Callback) o);
            }
        }
    }

    public void postEvent(FMLStateEvent event){
        if (event instanceof FMLConstructionEvent){
            //Not supported;
            return;
        }
        if (event instanceof FMLPreInitializationEvent){
            //Remove mod-specific information
            event = new FMLPreInitializationEvent(((FMLPreInitializationEvent) event).getAsmData(), ((FMLPreInitializationEvent) event).getModConfigurationDirectory());
        }
        if (!locked){
            locked = true;
        }
        postEventChecked(event);
    }

    private void postEventChecked(FMLStateEvent event){
        for (Callback callback : callbacks){
            callback.onEvent(event);
        }
    }

    static {
        callbacks = Sets.newHashSet();
        ElecCore.instance.setModEventHandler(new ModEventHandler());
    }

    public interface Callback {

        public void onEvent(FMLStateEvent event);

    }

}
