package elec332.core.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.common.Loader;
import elec332.core.network.NetworkHandler;
import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.registry.IWorldRegistry;
import elec332.core.util.BlockLoc;
import elec332.core.util.EventHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Elec332 on 27-7-2015.
 */
public final class MultiBlockRegistry extends AbstractWorldRegistryHolder<MultiBlockRegistry.MultiBlockWorldRegistry>{

    public MultiBlockRegistry(){
        this(new NetworkHandler(Loader.instance().activeModContainer().getModId()+"|MultiBlocks"));
    }

    public MultiBlockRegistry(NetworkHandler networkHandler){
        this.registry = Maps.newHashMap();
        this.networkHandler = networkHandler;
        this.structureRegistry = new MultiBlockStructureRegistry(this);
        EventHelper.registerHandlerForge(this);
    }

    @Override
     public boolean serverOnly() {
        return false;
    }

    @Override
    public MultiBlockWorldRegistry newRegistry(World world) {
        return new MultiBlockWorldRegistry(world);
    }

    private HashMap<Class<? extends IMultiBlockStructure>, Class<? extends IMultiBlock>> registry;
    private final MultiBlockStructureRegistry structureRegistry;
    protected final NetworkHandler networkHandler;


    public void registerMultiBlock(IMultiBlockStructure multiBlockStructure, String name, Class<? extends IMultiBlock> multiBlock){
        registry.put(multiBlockStructure.getClass(), multiBlock);
        structureRegistry.registerMultiBlockStructure(multiBlockStructure, name);
    }

    public MultiBlockStructureRegistry getStructureRegistry() {
        return this.structureRegistry;
    }

    /*@SubscribeEvent
    public void onChunkLoad(final ChunkDataEvent.Load event){
        ElecCore.tickHandler.registerCall(new Runnable() {
            @Override
            public void run() {
                MultiBlockWorldRegistry registry = get(event.world, false);
                if (registry != null){
                    List<IMultiBlockTile> list = registry.unloadedCache.get(event.getChunk().getChunkCoordIntPair());
                    if (list != null){
                        for (IMultiBlockTile tile : list){
                            IMultiBlock.tileEntityValidate(tile, tile.getMultiBlock(), MultiBlockRegistry.this);
                        }
                    }
                    registry.unloadedCache.put(event.getChunk().getChunkCoordIntPair(), null);
                }
            }
        });
    }*/

    public class MultiBlockWorldRegistry implements IWorldRegistry{

        protected MultiBlockWorldRegistry(World world){
            this.world = world;
            this.activeMultiBlocks = Lists.newArrayList();
            this.pausedMultiBlocks = Lists.newArrayList();
            //this.unloadedCache = Maps.newHashMap();
        }

        private final World world;
        private List<IMultiBlock> activeMultiBlocks;
        private List<IMultiBlock> pausedMultiBlocks;
        //private Map<ChunkCoordIntPair, List<IMultiBlockTile>> unloadedCache;

        /*private void addUnloadedTile(ChunkCoordIntPair coord, IMultiBlockTile tile){
            List<IMultiBlockTile> list = unloadedCache.get(coord);
            if (list == null){
                list = Lists.newArrayList();
                unloadedCache.put(coord, list);
            }
            list.add(tile);
        }*/

        protected World getWorldObj(){
            return world;
        }

        protected void invalidateMultiBlock(IMultiBlock multiBlock){
            for (BlockLoc loc : multiBlock.getAllMultiBlockLocations()){
                TileEntity tile = WorldHelper.getTileAt(world, loc);
                if (tile instanceof IMultiBlockTile){
                    ((IMultiBlockTile)tile).invalidateMultiBlock();
                }
            }
            this.activeMultiBlocks.remove(multiBlock);
            multiBlock.invalidate();
        }

        /*protected void tileChunkUnloaded(IMultiBlock multiBlock, IMultiBlockTile tile){
            //addUnloadedTile(WorldHelper.fromBlockLoc(new BlockLoc((TileEntity) tile)), tile);
            deactivateMultiBlock(multiBlock);
        }*/

        protected void deactivateMultiBlock(IMultiBlock multiBlock){
            this.activeMultiBlocks.remove(multiBlock);
            this.pausedMultiBlocks.add(multiBlock);
        }

        protected void reactivateMultiBlock(IMultiBlock multiBlock){
            this.pausedMultiBlocks.remove(multiBlock);
            this.activeMultiBlocks.add(multiBlock);
        }

        protected void createNewMultiBlock(IMultiBlockStructure multiBlockStructure, BlockLoc bottomLeft, List<BlockLoc> allLocations, World world, ForgeDirection facing){
            Class<? extends IMultiBlock> clazz = registry.get(multiBlockStructure.getClass());
            IMultiBlock multiBlock;
            try {
                multiBlock = clazz.getConstructor().newInstance();
            } catch (Exception e){
                throw new RuntimeException("Error invoking class: "+clazz.getName()+" Please make sure the constructor has no arguments!", e);
            }
            boolean one = false;
            for (BlockLoc loc : allLocations){
                TileEntity tile = WorldHelper.getTileAt(world, loc);
                if (tile instanceof IMultiBlockTile){
                    ((IMultiBlockTile)tile).setMultiBlock(multiBlock, facing, structureRegistry.getIdentifier(multiBlockStructure));
                    one = true;
                }
            }
            if (!one)
                throw new IllegalArgumentException("A multiblock must contain at LEAST 1 IMultiBlockTile");
            multiBlock.initMain(bottomLeft, facing, allLocations, this, structureRegistry.getIdentifier(multiBlockStructure));
            activeMultiBlocks.add(multiBlock);
        }

        /**
         * Gets called every tick
         */
        @Override
        public void tick() {
            for (IMultiBlock multiBlock : activeMultiBlocks){
                multiBlock.onTick();
            }
        }

        /**
         * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
         */
        @Override
        public void onWorldUnload() {
        }
    }

}
