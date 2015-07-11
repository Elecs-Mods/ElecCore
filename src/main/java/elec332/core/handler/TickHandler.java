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
        toGo = new ArrayDeque<Runnable>();
    }

    private Queue<Runnable> toGo;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        processSingleTicks(event);
    }

    @SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent event){
        processSingleTicks(event);
    }

    @Deprecated
    public void registerCall(final IRunOnce runnable){
        toGo.add(new Runnable() {
            @Override
            public void run() {
                runnable.run();
            }
        });
    }

    public void registerCall(final Runnable runnable){
        toGo.add(runnable);
    }

    private void processSingleTicks(TickEvent event){
        if (event.phase == TickEvent.Phase.START && !toGo.isEmpty()) {
            for (Runnable runnable = toGo.poll(); runnable != null; runnable = toGo.poll()) {
                runnable.run();
            }
            toGo.clear();
        }
    }
}
