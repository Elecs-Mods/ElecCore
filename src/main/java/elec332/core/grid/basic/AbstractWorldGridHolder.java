package elec332.core.grid.basic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.registry.IWorldRegistry;
import elec332.core.world.WorldHelper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractWorldGridHolder<A extends AbstractWorldGridHolder<A, G, T, W>, G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T, W, A>, W extends IWiringTypeHelper> implements IWorldRegistry{

    public AbstractWorldGridHolder(World world, W wiringHelper){
        this.world = world;
        this.grids = Lists.newArrayList();
        this.registeredTiles = Maps.newHashMap();
        this.pending = new ArrayDeque<T>();
        this.oldInt = 0;
        this.wiringHelper = wiringHelper;
    }

    private World world;  //Dunno why I have this here (yet)
    private List<G> grids;
    private Queue<T> pending;
    private Map<BlockPos, T> registeredTiles;
    private int oldInt;
    private W wiringHelper;

    public G registerGrid(G grid){
        this.grids.add(grid);
        return grid;
    }

    protected void removeGrid(G grid){
        grid.invalidate();
        grids.remove(grid);
    }

    public void addTile(TileEntity tile){
        T powerTile = newGridTile(tile);
        registeredTiles.put(genCoords(tile), powerTile);
        addTile(powerTile);
        ElecCore.systemPrintDebug("Tile placed at " + genCoords(tile));
    }

    protected abstract T newGridTile(TileEntity tile);

    public void addTile(T powerTile){
        if(!world.isRemote) {
            ElecCore.systemPrintDebug("Processing tile at " + powerTile.getLocation().toString());
            TileEntity theTile = powerTile.getTile();
            for (EnumFacing direction : EnumFacing.VALUES) {
                ElecCore.systemPrintDebug("Processing tile at " + powerTile.getLocation().toString() + " for side " + direction.toString());
                TileEntity possTile = getTile(theTile.getPos().offset(direction));
                if (possTile != null && wiringHelper.isTileValid(possTile)) {
                    T powerTile1 = getPowerTile(genCoords(possTile));
                    if (powerTile1 == null || !powerTile1.hasInit()) {
                        pending.add(powerTile);
                        break;
                    }
                    if (canConnect(powerTile, direction, powerTile1)) {
                        G grid = powerTile1.getGridFromSide(direction.getOpposite());
                        powerTile.getGridFromSide(direction).mergeGrids(grid);
                    }
                } else {
                    ElecCore.systemPrintDebug("There is no tile at side " + direction.toString() + " that is valid for connection");
                }
            }
        }
    }

    private boolean canConnect(T powerTile1, EnumFacing direction, T powerTile2){
        TileEntity mainTile = powerTile1.getTile();
        boolean flag1 = false;
        boolean flag2 = false;
        if (powerTile1.getConnectType() == powerTile2.getConnectType() && (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.SEND || powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.RECEIVE))
            return false; //We don't want to receivers or 2 sources connecting, do we?
        if (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.CONNECTOR && powerTile2.getConnectType() == IWiringTypeHelper.ConnectType.CONNECTOR){
            return wiringHelper.canTransmitterConnectTo(mainTile, powerTile2.getTile()) && wiringHelper.canTransmitterConnectTo(mainTile, direction) && canConnectFromSide(direction.getOpposite(), powerTile2);
        } else if (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.CONNECTOR){
            return canConnectFromSide(direction.getOpposite(), powerTile2);
        } else {
            if (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.SEND_RECEIVE){
                if (wiringHelper.canSourceProvideTo(mainTile, direction))
                    flag1 = canConnectFromSide(direction.getOpposite(), powerTile2);
                if (wiringHelper.canReceiverReceiveFrom(mainTile, direction))
                    flag2 = canConnectFromSide(direction.getOpposite(), powerTile2);
                return flag1 || flag2;
            } else if (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.RECEIVE){
                if (wiringHelper.canReceiverReceiveFrom(mainTile, direction))
                    return canConnectFromSide(direction.getOpposite(), powerTile2);
            } else if (powerTile1.getConnectType() == IWiringTypeHelper.ConnectType.SEND){
                if (wiringHelper.canSourceProvideTo(mainTile, direction))
                    return canConnectFromSide(direction.getOpposite(), powerTile2);
            }
            return false;
        }
    }

    private boolean canConnectFromSide(EnumFacing direction, T powerTile2){
        TileEntity secondTile = powerTile2.getTile();
        boolean flag1 = false;
        boolean flag2 = false;
        if (wiringHelper.isTransmitter(secondTile))
            return wiringHelper.canTransmitterConnectTo(secondTile, direction);
        if (wiringHelper.isReceiver(secondTile))
            flag1 = wiringHelper.canReceiverReceiveFrom(secondTile, direction);
        if (wiringHelper.isSource(secondTile))
            flag2 = wiringHelper.canSourceProvideTo(secondTile, direction);
        return flag1 || flag2;
    }

    public void removeTile(TileEntity tile){
        if (!wiringHelper.isTileValid(tile))
            throw new IllegalArgumentException();
        removeTile(getPowerTile(genCoords(tile)));
    }

    public void removeTile(T powerTile){
        if (powerTile != null) {
            for (G grid : powerTile.getGrids()) {
                if (grid != null) {
                    ElecCore.systemPrintDebug("Removing tile at " + powerTile.getLocation().toString());
                    List<BlockPos> vec3List = Lists.newArrayList();
                    vec3List.addAll(grid.getLocations());
                    vec3List.remove(powerTile.getLocation());
                    ElecCore.systemPrintDebug(grids.size());
                    grid.onTileRemoved(powerTile);
                    removeGrid(grid);
                    ElecCore.systemPrintDebug(grids.size());
                    ElecCore.systemPrintDebug(registeredTiles.keySet().size());
                    registeredTiles.remove(powerTile.getLocation());
                    ElecCore.systemPrintDebug(registeredTiles.keySet().size());
                    List<BlockPos> vec3List2 = Lists.newArrayList();
                    for (BlockPos vec : vec3List) {
                        if (!vec.equals(powerTile.getLocation())) {
                            T pt = getPowerTile(vec);
                            if (pt != null) {
                                pt.resetGrid(grid);
                                vec3List2.add(vec);
                            }
                        }
                    }
                    for (BlockPos vec : vec3List2) {
                        ElecCore.systemPrintDebug("Re-adding tile at " + vec.toString());
                        TileEntity tileEntity1 = getTile(vec);
                        if (wiringHelper.isTileValid(tileEntity1))
                            if (getPowerTile(vec) != null)
                                addTile(getPowerTile(vec));
                    }
                }
            }
        }
    }

    @Override
    public void tick() {
        onServerTickInternal();
    }

    private void onServerTickInternal(){
        ElecCore.systemPrintDebug("Tick! " + WorldHelper.getDimID(world));
        if (!pending.isEmpty() && pending.size() == oldInt) {
            for (T powerTile = pending.poll(); powerTile != null; powerTile = pending.poll()){
                addTile(powerTile);
            }
            pending.clear();
        }
        this.oldInt = pending.size();
        for (int i = 0; i < grids.size(); i++){
            try {
                grids.get(i).onTick();
            } catch (Throwable t){
                t.printStackTrace();
            }
            ElecCore.systemPrintDebug(i);
        }
    }

    public T getPowerTile(BlockPos loc){
        return registeredTiles.get(loc);
    }

    private BlockPos genCoords(TileEntity tileEntity){
        return new BlockPos(tileEntity.getPos());
    }

    private TileEntity getTile(BlockPos vec){
        return WorldHelper.chunkLoaded(world, vec) ? WorldHelper.getTileAt(world, vec) : null;
    }

    @Override
    public void onWorldUnload() {
    }

}
