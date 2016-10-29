package elec332.core.grid.internal;

import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

/**
 * Created by Elec332 on 23-7-2016.
 */
@SuppressWarnings("unused")
public class GridEventHandler {

    @SubscribeEvent
    public void loadWorld(WorldEvent.Load event){
        World world = event.getWorld();
        if (!world.isRemote){
            world.removeEventListener(WorldEventHandler.INSTANCE);
            world.addEventListener(WorldEventHandler.INSTANCE);
        }
    }

    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event){
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.chunkLoad(event.getChunk());
        }
    }

    @SubscribeEvent
    public void chunkUnLoad(ChunkEvent.Unload event){
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.chunkUnLoad(event.getChunk());
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onNeighborChange(BlockEvent.NeighborNotifyEvent event){
        if (!event.getWorld().isRemote) {
            GridEventInputHandler.INSTANCE.onBlockNotify(event.getWorld(), event.getPos(), event.getState());
        }
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event){
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            GridEventInputHandler.INSTANCE.tickEnd();
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event){
        World world = event.getWorld();
        if (!world.isRemote){
            GridEventInputHandler.INSTANCE.worldUnload(world);
        }
    }

}
