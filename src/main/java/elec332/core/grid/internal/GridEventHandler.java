package elec332.core.grid.internal;

import elec332.core.api.annotations.StaticLoad;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Elec332 on 23-7-2016.
 */
@StaticLoad
class GridEventHandler {

    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event) {
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.chunkLoad(event.getChunk());
        }
    }

    @SubscribeEvent
    public void chunkUnLoad(ChunkEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.chunkUnLoad(event.getChunk());
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onNeighborChange(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.onBlockNotify(event.getWorld(), event.getPos(), event.getState());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            GridEventInputHandler.INSTANCE.tickEnd();
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        World world = event.getWorld();
        if (!world.isRemote) {
            GridEventInputHandler.INSTANCE.worldUnload(world);
        }
    }

    @SubscribeEvent
    public void worldLoad(WorldEvent.Load event){
        World world = event.getWorld();
        if (!world.isRemote) {
            world.removeEventListener(WorldEventHandler.INSTANCE);
            world.addEventListener(WorldEventHandler.INSTANCE);
        }
    }

    static {
        MinecraftForge.EVENT_BUS.register(new GridEventHandler());
    }

}
