package elec332.core.handler;

import elec332.core.main.ElecCore;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Elec332 on 29-4-2015.
 */
public class TickHandler {

    public TickHandler(){
        toGo = new ArrayDeque<Runnable>();
        client = new ArrayDeque<Runnable>();
        server = new ArrayDeque<Runnable>();
    }

    private Queue<Runnable> toGo;
    private Queue<Runnable> client;
    private Queue<Runnable> server;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        processSingleTicks(event, server);
        processSingleTicks(event, toGo);
    }

    @SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent event){
        processSingleTicks(event, client);
        processSingleTicks(event, toGo);
    }

    @Deprecated
    public void registerCall(final Runnable runnable){
        if (runnable == null)
            return;
        toGo.add(runnable);
        ElecCore.logger.info("##############################################");
        ElecCore.logger.info("## Error, someone is using an unsafe method!##");
        ElecCore.logger.info("##############################################");
        ElecCore.logger.info("Source: "+runnable.getClass().toString());
    }

    @SuppressWarnings("deprecation")
    public void registerCall(Runnable runnable, World world){
        if (world == null){
            registerCall(runnable);
            return;
        }
        if (!world.isRemote){
            registerCallServer(runnable);
        } else {
            registerCallClient(runnable);
        }
    }

    @SuppressWarnings("deprecation")
    public void registerCall(Runnable runnable, Side side){
        if (side == null){
            registerCall(runnable);
            return;
        }
        if (side.isServer()){
            registerCallServer(runnable);
        } else if (side.isClient()){
            registerCallClient(runnable);
        } else {
            throw new IllegalArgumentException("What am I supposed to do with side: "+side+"?");
        }
    }

    public void registerCallServer(Runnable runnable){
        server.add(runnable);
    }

    public void registerCallClient(Runnable runnable){
        client.add(runnable);
    }

    private void processSingleTicks(TickEvent event, Queue<Runnable> process){
        if (event.phase == TickEvent.Phase.START && !process.isEmpty()) {
            for (Runnable runnable = process.poll(); runnable != null; runnable = process.poll()) {
                runnable.run();
            }
            process.clear();
        }
    }
}
