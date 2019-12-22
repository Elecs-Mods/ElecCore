package elec332.core.handler;

import com.google.common.base.Preconditions;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * Created by Elec332 on 29-4-2015.
 */
@SuppressWarnings("all")
public class TickHandler {

    public TickHandler() {
        clientCallbacks = new ArrayDeque<Runnable>();
        serverCallbacks = new ArrayDeque<Runnable>();
        clientTickables = new ArrayDeque<Runnable>();
        serverTickables = new ArrayDeque<Runnable>();
        MinecraftForge.EVENT_BUS.register(new Object() {

            @SubscribeEvent
            public void onTick(TickEvent.ServerTickEvent event) {
                processSingleTicks(event, serverCallbacks);
                processTickables(event, serverTickables);
            }

            @SubscribeEvent
            public void onTickClient(TickEvent.ClientTickEvent event) {
                processSingleTicks(event, clientCallbacks);
                processTickables(event, clientTickables);
            }

        });
    }

    private final Queue<Runnable> clientCallbacks, serverCallbacks;
    private final Queue<Runnable> clientTickables, serverTickables;

    public void registerCall(Runnable runnable, World world) {
        register(runnable, Preconditions.checkNotNull(world).isRemote, clientCallbacks, serverCallbacks);
    }

    public void registerCall(Runnable runnable, LogicalSide side) {
        register(runnable, Preconditions.checkNotNull(side) == LogicalSide.CLIENT, clientCallbacks, serverCallbacks);
    }

    public void registerTickable(Runnable runnable, World world) {
        register(runnable, Preconditions.checkNotNull(world).isRemote, clientTickables, serverTickables);
    }

    public void registerTickable(Runnable runnable, LogicalSide side) {
        register(runnable, Preconditions.checkNotNull(side) == LogicalSide.CLIENT, clientTickables, serverTickables);
    }

    public void registerCallServer(Runnable runnable) {
        serverCallbacks.add(Preconditions.checkNotNull(runnable));
    }

    public void registerCallClient(Runnable runnable) {
        clientCallbacks.add(Preconditions.checkNotNull(runnable));
    }

    public void registerServerTickable(Runnable runnable) {
        serverTickables.add(runnable);
    }

    public void registerClientTickable(Runnable runnable) {
        clientTickables.add(runnable);
    }

    public void removeTickable(Runnable runnable) {
        serverTickables.remove(runnable);
        clientTickables.remove(runnable);
    }

    private void register(Runnable runnable, boolean client, Queue<Runnable> clC, Queue<Runnable> srvC) {
        (client ? clC : srvC).add(Preconditions.checkNotNull(runnable));
    }

    private void processSingleTicks(TickEvent event, Queue<Runnable> process) {
        if (event.phase == TickEvent.Phase.START && !process.isEmpty()) {
            for (Runnable runnable = process.poll(); runnable != null; runnable = process.poll()) {
                runnable.run();
            }
            process.clear();
        }
    }

    private void processTickables(TickEvent event, Queue<Runnable> process) {
        if (event.phase == TickEvent.Phase.START && !process.isEmpty()) {
            for (Runnable runnable : process) {
                runnable.run();
            }
        }
    }

}
