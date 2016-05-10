package elec332.core.multiblock.dynamic;

import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import elec332.core.registry.IWorldRegistry;
import elec332.core.world.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;


/**
 * Created by Elec332 on 9-8-2015.
 */
public abstract class AbstractDynamicMultiBlockWorldHolder<A extends AbstractDynamicMultiBlockWorldHolder<A, M>, M extends AbstractDynamicMultiBlock<A, M>> implements IWorldRegistry {

    public AbstractDynamicMultiBlockWorldHolder(World world){
        this.world = world;
        this.registeredTiles = Lists.newArrayList();
        this.pending = new ArrayDeque<BlockPos>();
        this.multiBlocks = Lists.newArrayList();
        this.oldInt = 0;
    }

    private World world;
    private List<BlockPos> registeredTiles;
    private Queue<BlockPos> pending;
    private List<M> multiBlocks;
    private int oldInt;

    public final M registerGrid(M m){
        if (multiBlocks.contains(m))
            throw new IllegalArgumentException("MultiBlock is already registered!");
        multiBlocks.add(m);
        return m;
    }

    protected void removeGrid(M m){
        m.invalidate();
        multiBlocks.remove(m);
    }

    @SuppressWarnings("unchecked")
    public M getMultiBlock(IDynamicMultiBlockTile tile){
        return ((IDynamicMultiBlockTile<M>)tile).getMultiBlock();
    }

    @SuppressWarnings("unchecked")
    public void setMultiBlock(IDynamicMultiBlockTile tile, M m){
        ((IDynamicMultiBlockTile<M>)tile).setMultiBlock(m);
    }

    public abstract boolean isTileValid(TileEntity tile);

    public abstract boolean canConnect(TileEntity main, EnumFacing direction, TileEntity otherTile);

    public abstract M newMultiBlock(TileEntity tile);

    public void addTile(TileEntity tile){
        addTile(tile, false);
    }

    private void addTile(TileEntity tile, boolean recreate){
        if (tile== null){
            System.out.println("ERROR, NULL TILE!");
            return;
        }
        if (!isTileValid(tile) || !(tile instanceof IDynamicMultiBlockTile))
            throw new IllegalArgumentException("Invalid tile");
        if (!WorldHelper.chunkLoaded(world, tile.getPos())) {
            return;
        }
        if(!world.isRemote && getMultiBlock((IDynamicMultiBlockTile) tile) == null) {
            BlockPos loc = new BlockPos(tile.getPos());
            registeredTiles.add(loc);
            M newM = registerGrid(newMultiBlock(tile));
            setMultiBlock((IDynamicMultiBlockTile) tile, newM);
            for (EnumFacing direction : EnumFacing.VALUES) {
                TileEntity possTile = WorldHelper.getTileAt(world, loc.offset(direction));
                if (possTile != null && possTile instanceof IDynamicMultiBlockTile && isTileValid(possTile)) {
                    BlockPos fromPossTile = new BlockPos(possTile.getPos());
                    if (!registeredTiles.contains(fromPossTile)) {
                        continue;
                    }
                    if (canConnect(tile, direction, possTile)) {
                        M mb = getMultiBlock((IDynamicMultiBlockTile) possTile);
                        if (mb != null && mb.getClass() != newM.getClass())
                            throw new RuntimeException("Weird mess of multiblock types.");
                        if (mb == null) {
                            System.out.println("Something weird was detected, fixing now...");
                            M weirdM = registerGrid(newMultiBlock(possTile));
                            setMultiBlock((IDynamicMultiBlockTile)possTile, weirdM);
                            mb = getMultiBlock((IDynamicMultiBlockTile) possTile);
                        }
                        if (mb == null)
                            throw new RuntimeException("I have no idea what kind of weird stuff happened here...");
                        if (!newM.equals(mb)) {
                            for (BlockPos mbLoc : mb.getAllLocations()) {
                                setMultiBlock((IDynamicMultiBlockTile) WorldHelper.getTileAt(world, mbLoc), newM);
                            }
                            newM.mergeWith(mb);
                            mb.invalidate();
                            removeGrid(mb);
                        }
                    }
                } else {
                    ElecCore.systemPrintDebug("There is no tile at side " + direction.toString() + " that is valid for connection");
                }
            }
        } else if (!world.isRemote){
            System.out.println("------------------------------------");
            System.out.println("ERROR!!!  Tile at "+tile.getPos()+" is trying to register whilst already being registered!");
            System.out.println("------------------------------------");
        }
    }

    public void removeTile(TileEntity tile) {
        if (tile == null || !isTileValid(tile) || !(tile instanceof IDynamicMultiBlockTile))
            throw new IllegalArgumentException("Invalid tile");
        M m = getMultiBlock((IDynamicMultiBlockTile) tile);
        if (m != null) {
            List<BlockPos> vec3List = Lists.newArrayList(m.getAllLocations());
            m.onTileRemoved(tile);
            removeGrid(m);
            registeredTiles.removeAll(vec3List);
            vec3List.remove(tile.getPos());
            for (BlockPos vec : vec3List) {
                if (WorldHelper.chunkLoaded(world, vec)) {
                    TileEntity mbTile = WorldHelper.getTileAt(world, vec);
                    if (mbTile != null) {
                        setMultiBlock((IDynamicMultiBlockTile) mbTile, null);
                    }
                }
            }
            setMultiBlock((IDynamicMultiBlockTile)tile, null);
            for (BlockPos vec : vec3List) {
                if (WorldHelper.chunkLoaded(world, vec)) {
                    TileEntity tileEntity1 = WorldHelper.getTileAt(world, vec);
                    addTile(tileEntity1);
                }
            }
        }
    }

    @Override
    public void tick() {
        onServerTickInternal();
    }

    private void onServerTickInternal(){
        if (!pending.isEmpty() && pending.size() == oldInt) {
            for (BlockPos loc = pending.poll(); loc != null; loc = pending.poll()) {
                addTile(WorldHelper.getTileAt(world, loc));
            }
            pending.clear();
        }
        this.oldInt = pending.size();
        for (M m : multiBlocks){
            m.tick();
        }
    }

    @Override
    public void onWorldUnload() {
    }

}
