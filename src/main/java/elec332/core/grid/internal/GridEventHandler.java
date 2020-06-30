package elec332.core.grid.internal;

import elec332.core.api.APIHandlerInject;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.world.IWorldGenManager;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

/**
 * Created by Elec332 on 23-7-2016.
 */
@StaticLoad
class GridEventHandler {

    @SubscribeEvent
    public void chunkLoad(ChunkEvent.Load event) {
        if (event.getWorld() != null && !event.getWorld().isRemote()) {
            if (!(event.getChunk() instanceof Chunk)) {
                throw new RuntimeException();
            }
            GridEventInputHandler.INSTANCE.chunkLoad((Chunk) event.getChunk());
        }
    }

    @SubscribeEvent
    public void chunkUnLoad(ChunkEvent.Unload event) {
        if (!event.getWorld().isRemote()) {
            if (!(event.getChunk() instanceof Chunk)) {
                throw new RuntimeException();
            }
            GridEventInputHandler.INSTANCE.chunkUnLoad((Chunk) event.getChunk());
        }
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onNeighborChange(BlockEvent.NeighborNotifyEvent event) {
        if (!event.getWorld().isRemote()) {
            GridEventInputHandler.INSTANCE.onBlockNotify(event.getWorld(), event.getPos(), event.getState());
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void serverTick(TickEvent.ServerTickEvent event) {
        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END) {
            GridEventInputHandler.INSTANCE.tickEnd();
        }
    }

    @SubscribeEvent
    public void worldUnload(WorldEvent.Unload event) {
        if (!(event.getWorld() instanceof World)) {
            throw new RuntimeException();
        }
        World world = (World) event.getWorld();
        if (!world.isRemote()) {
            GridEventInputHandler.INSTANCE.worldUnload(world);
        }
    }

//    @SubscribeEvent
//    public void worldLoad(WorldEvent.Load event) {
//        if (!(event.getWorld() instanceof World)) {
//            throw new RuntimeException();
//        }
//        World world = (World) event.getWorld();
//        if (!world.isRemote()) {
//            //world.removeEventListener(WorldEventHandler.INSTANCE);
//            //world.addEventListener(WorldEventHandler.INSTANCE);
//            ServerChunkProvider scp = (ServerChunkProvider) world.getChunkProvider();
////            if (!(scp instanceof WrappedServerChunkProvider)) {
////                world.chunkProvider = new WrappedServerChunkProvider(scp);
////            }
//        }
//    }

    @APIHandlerInject
    private static void registerBlockChangedHook(IWorldGenManager worldGenManager) {
        worldGenManager.registerBlockChangedHook(GridEventInputHandler.INSTANCE::worldBlockUpdate);
    }

    static {
        MinecraftForge.EVENT_BUS.register(new GridEventHandler());
    }

}
