package elec332.core.multiblock;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.api.network.INetworkHandler;
import elec332.core.client.RenderHelper;
import elec332.core.main.ElecCore;
import elec332.core.network.impl.NetworkManager;
import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.registry.IWorldRegistry;
import elec332.core.util.FMLUtil;
import elec332.core.world.WorldHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 27-7-2015.
 */
public final class MultiBlockRegistry extends AbstractWorldRegistryHolder<MultiBlockRegistry.MultiBlockWorldRegistry>{

    public MultiBlockRegistry(){
        this(NetworkManager.INSTANCE.getNetworkHandler(FMLUtil.getLoader().activeModContainer()));
    }

    public MultiBlockRegistry(INetworkHandler networkHandler){
        this.registry = Maps.newHashMap();
        this.multiBlockRendererMap = Maps.newHashMap();
        this.networkHandler = networkHandler;
        this.structureRegistry = new MultiBlockStructureRegistry(this);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
     public boolean serverOnly() {
        return false;
    }

    @Override
    public MultiBlockWorldRegistry newRegistry(World world) {
        return new MultiBlockWorldRegistry(world);
    }

    private Map<Class<? extends IMultiBlockStructure>, Class<? extends IMultiBlock>> registry;
    private Map<Class<? extends IMultiBlock>, IMultiBlockRenderer<? extends IMultiBlock>> multiBlockRendererMap;
    private final MultiBlockStructureRegistry structureRegistry;
    protected final INetworkHandler networkHandler;


    public void registerMultiBlock(IMultiBlockStructure multiBlockStructure, String name, Class<? extends IMultiBlock> multiBlock){
        registry.put(multiBlockStructure.getClass(), multiBlock);
        structureRegistry.registerMultiBlockStructure(multiBlockStructure, name);
    }

    public <M extends IMultiBlock> void registerMultiBlockRenderer(Class<M> clazz, IMultiBlockRenderer<M> renderer){
        multiBlockRendererMap.put(clazz, renderer);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    @SuppressWarnings({"unchecked", "unused"})
    public void renderMultiBlocks(RenderWorldLastEvent event){
        MultiBlockWorldRegistry mbwr = get(ElecCore.proxy.getClientWorld(), false);
        if (mbwr != null){
            ICamera camera = RenderHelper.getPlayerCamera(event.getPartialTicks());
            for (IMultiBlock multiBlock : mbwr.activeMultiBlocks){
                IMultiBlockRenderer mbr = multiBlockRendererMap.get(multiBlock.getClass());
                if (mbr != null && camera.isBoundingBoxInFrustum(mbr.getRenderingBoundingBox(multiBlock))) {
                    GlStateManager.pushMatrix();
                    RenderHelper.translateToWorld(event.getPartialTicks());
                    mbr.renderMultiBlock(multiBlock, event.getPartialTicks());
                    GlStateManager.popMatrix();
                }
            }
        }
    }

    public MultiBlockStructureRegistry getStructureRegistry() {
        return this.structureRegistry;
    }

    public class MultiBlockWorldRegistry implements IWorldRegistry {

        protected MultiBlockWorldRegistry(World world){
            this.world = world;
            this.activeMultiBlocks = Lists.newArrayList();
            this.pausedMultiBlocks = Lists.newArrayList();
        }

        private final World world;
        private List<IMultiBlock> activeMultiBlocks;
        private List<IMultiBlock> pausedMultiBlocks;

        protected World getWorldObj(){
            return world;
        }

        protected void invalidateMultiBlock(IMultiBlock multiBlock){
            for (BlockPos loc : multiBlock.getAllMultiBlockLocations()){
                TileEntity tile = WorldHelper.getTileAt(world, loc);
                if (tile instanceof IMultiBlockTile){
                    ((IMultiBlockTile)tile).invalidateMultiBlock();
                }
            }
            this.activeMultiBlocks.remove(multiBlock);
            multiBlock.invalidate();
        }

        protected void deactivateMultiBlock(IMultiBlock multiBlock){
            this.activeMultiBlocks.remove(multiBlock);
            this.pausedMultiBlocks.add(multiBlock);
        }

        protected void reactivateMultiBlock(IMultiBlock multiBlock){
            this.pausedMultiBlocks.remove(multiBlock);
            this.activeMultiBlocks.add(multiBlock);
        }

        protected void createNewMultiBlock(IMultiBlockStructure multiBlockStructure, BlockPos bottomLeft, List<BlockPos> allLocations, World world, EnumFacing facing){
            Class<? extends IMultiBlock> clazz = registry.get(multiBlockStructure.getClass());
            IMultiBlock multiBlock;
            try {
                multiBlock = clazz.getConstructor().newInstance();
            } catch (Exception e){
                throw new RuntimeException("Error invoking class: "+clazz.getName()+" Please make sure the constructor has no arguments!", e);
            }
            multiBlock.initMain(bottomLeft, facing, allLocations, this, structureRegistry.getIdentifier(multiBlockStructure));
            boolean one = false;
            for (BlockPos loc : allLocations){
                TileEntity tile = WorldHelper.getTileAt(world, loc);
                if (tile instanceof IMultiBlockTile){
                    ((IMultiBlockTile)tile).setMultiBlock(multiBlock, facing, structureRegistry.getIdentifier(multiBlockStructure));
                    one = true;
                }
            }
            if (!one) {
                throw new IllegalArgumentException("A multiblock must contain at LEAST 1 IMultiBlockTile");
            }
            activeMultiBlocks.add(multiBlock);
        }

        /**
         * Gets called every tick
         */
        @Override
        public void tick() {
            for (IMultiBlock multiBlock : new Iterable<IMultiBlock>() {
                @Override
                public Iterator<IMultiBlock> iterator() {
                    return activeMultiBlocks.listIterator();
                }
            }){
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
