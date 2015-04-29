package elec332.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import elec332.core.util.IRunOnce;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Elec332 on 29-4-2015.
 */
public class TickHandler {
    private static List<IRunOnce> toGo = new ArrayList<IRunOnce>();

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        processSingleTicks(event);
    }


    @SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent event){
        processSingleTicks(event);
    }

    public static void registerCall(IRunOnce runnable){
        toGo.add(runnable);
    }

    private void processSingleTicks(TickEvent event){
        if (event.phase == TickEvent.Phase.END)
            for (IRunOnce runnable : toGo)
                runnable.run();
        toGo.clear();
    }
}
