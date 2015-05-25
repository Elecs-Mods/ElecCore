package elec332.core.handler;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import elec332.core.util.IRunOnce;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Elec332 on 29-4-2015.
 */
public class TickHandler {

    public TickHandler(){
        toGo = new ArrayDeque<IRunOnce>();
    }
    private Queue<IRunOnce> toGo;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        processSingleTicks(event);
    }

    @SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent event){
        processSingleTicks(event);
    }

    public void registerCall(IRunOnce runnable){
        toGo.add(runnable);
    }

    private void processSingleTicks(TickEvent event){
        if (event.phase == TickEvent.Phase.START && !toGo.isEmpty()) {
            for (IRunOnce runnable = toGo.poll(); runnable != null; runnable = toGo.poll()) {
                runnable.run();
            }
            toGo.clear();
        }
    }
}
