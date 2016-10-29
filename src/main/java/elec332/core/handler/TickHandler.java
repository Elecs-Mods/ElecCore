package elec332.core.handler;

import elec332.core.main.ElecCore;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Elec332 on 29-4-2015.
 */
@SuppressWarnings("all")
public class TickHandler {

    public TickHandler(){
        unknownCallbacks = new ArrayDeque<Runnable>();
        clientCallbacks = new ArrayDeque<Runnable>();
        serverCallbacks = new ArrayDeque<Runnable>();
        unknownTickables = new ArrayDeque<Runnable>();
        clientTickables = new ArrayDeque<Runnable>();
        serverTickables = new ArrayDeque<Runnable>();
        mainSide = FMLCommonHandler.instance().getSide();
    }

    private final Side mainSide;
    private final Queue<Runnable> unknownCallbacks, clientCallbacks, serverCallbacks;
    private final Queue<Runnable> unknownTickables, clientTickables, serverTickables;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event){
        processSingleTicks(event, serverCallbacks);
        processTickables(event, serverTickables);
        if (mainSide.isServer()) {
            processTickables(event, unknownTickables);
            processSingleTicks(event, unknownCallbacks);
        }
    }

    @SubscribeEvent
    public void onTickClient(TickEvent.ClientTickEvent event){
        processSingleTicks(event, clientCallbacks);
        processTickables(event, clientTickables);
        if (mainSide.isClient()) {
            processSingleTicks(event, unknownCallbacks);
            processTickables(event, unknownTickables);
        }
    }

    @Deprecated
    public void registerCall(final Runnable runnable){
        if (runnable == null)
            return;
        unknownCallbacks.add(runnable);
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
        serverCallbacks.add(runnable);
    }

    public void registerCallClient(Runnable runnable){
        clientCallbacks.add(runnable);
    }

    public void registerTickable(Runnable runnable, World world){
        if (runnable == null) {
            return;
        }
        if (world == null) {
            unknownTickables.add(runnable);
            ElecCore.logger.info("##############################################");
            ElecCore.logger.info("## Error, someone is using an unsafe method!##");
            ElecCore.logger.info("##############################################");
            ElecCore.logger.info("Source: " + runnable.getClass().toString());
            return;
        }
        registerTickable(runnable, world.isRemote ? Side.CLIENT : Side.SERVER);
    }

    public void registerTickable(Runnable runnable, Side side){
        if (runnable == null) {
            return;
        }
        if (side == null){
            registerTickable(runnable, (World) null);
            return;
        }
        if (side.isServer()){
            registerServerTickable(runnable);
        } else if (side.isClient()){
            registerClientTickable(runnable);
        } else {
            throw new IllegalArgumentException("What am I supposed to do with side: "+side+"?");
        }
    }

    public void registerServerTickable(Runnable runnable){
        serverTickables.add(runnable);
    }

    public void registerClientTickable(Runnable runnable){
        clientTickables.add(runnable);
    }

    public void removeTickable(Runnable runnable){
        serverTickables.remove(runnable);
        clientTickables.remove(runnable);
        unknownTickables.remove(runnable);
    }

    private void processSingleTicks(TickEvent event, Queue<Runnable> process){
        if (event.phase == TickEvent.Phase.START && !process.isEmpty()) {
            for (Runnable runnable = process.poll(); runnable != null; runnable = process.poll()) {
                runnable.run();
            }
            process.clear();
        }
    }

    private void processTickables(TickEvent event, Queue<Runnable> process){
        if (event.phase == TickEvent.Phase.START && !process.isEmpty()) {
            for (Runnable runnable : process){
                runnable.run();
            }
        }
    }

}
