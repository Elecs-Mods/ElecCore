package elec332.core.grid.basic;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.*;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractWorldGridHolder<A extends AbstractWorldGridHolder, G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T>, W extends AbstractWiringTypeHelper> {

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
    //private List<PowerTile> pendingRemovals;
    private Map<BlockLoc, T> registeredTiles;
    private int oldInt;
    private W wiringHelper;

    public G registerGrid(G grid){
        this.grids.add(grid);
        return grid;
    }

    protected void removeGrid(G grid){
        grids.remove(grid);
    }

    public void addTile(TileEntity tile){
        T powerTile = newGridTile(tile);
        registeredTiles.put(genCoords(tile), powerTile);
        addTile(powerTile);
        ElecCore.systemPrintDebug("Tile placed at " + genCoords(tile).toString());
    }

    protected abstract boolean isTileValid(TileEntity tile);

    protected abstract T newGridTile(TileEntity tile);

    public void addTile(T powerTile){
        if(!world.isRemote) {
            ElecCore.systemPrintDebug("Processing tile at " + powerTile.getLocation().toString());
            TileEntity theTile = powerTile.getTile();
            for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
                ElecCore.systemPrintDebug("Processing tile at " + powerTile.getLocation().toString() + " for side " + direction.toString());
                TileEntity possTile = world.getTileEntity(theTile.xCoord + direction.offsetX, theTile.yCoord + direction.offsetY, theTile.zCoord + direction.offsetZ);
                if (possTile != null && isTileValid(possTile)) {
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

    private boolean canConnect(T powerTile1, ForgeDirection direction, T powerTile2){
        TileEntity mainTile = powerTile1.getTile();
        boolean flag1 = false;
        boolean flag2 = false;
        if (powerTile1.getConnectType() == powerTile2.getConnectType() && (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.SEND || powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.RECEIVE))
            return false; //We don't want to receivers or 2 sources connecting, do we?
        if (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.CONNECTOR && powerTile2.getConnectType() == AbstractWiringTypeHelper.ConnectType.CONNECTOR){
            return wiringHelper.canTransmitterConnectTo(mainTile, powerTile2.getTile());
        } else if (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.CONNECTOR){
            return canConnectFromSide(direction.getOpposite(), powerTile2);
        } else {
            if (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.SEND_RECEIVE){
                if (wiringHelper.canSourceProvideTo(mainTile, direction))
                    flag1 = canConnectFromSide(direction.getOpposite(), powerTile2);
                if (wiringHelper.canReceiverReceiveFrom(mainTile, direction))
                    flag2 = canConnectFromSide(direction.getOpposite(), powerTile2);
                return flag1 || flag2;
            } else if (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.RECEIVE){
                if (wiringHelper.canReceiverReceiveFrom(mainTile, direction))
                    return canConnectFromSide(direction.getOpposite(), powerTile2);
            } else if (powerTile1.getConnectType() == AbstractWiringTypeHelper.ConnectType.SEND){
                if (wiringHelper.canSourceProvideTo(mainTile, direction))
                    return canConnectFromSide(direction.getOpposite(), powerTile2);
            }
            return false;
        }
    }

    private boolean canConnectFromSide(ForgeDirection direction, T powerTile2){
        TileEntity secondTile = powerTile2.getTile();
        boolean flag1 = false;
        boolean flag2 = false;
        if (wiringHelper.isTransmitter(secondTile))
            return true;
        if (wiringHelper.isReceiver(secondTile))
            flag1 = wiringHelper.canReceiverReceiveFrom(secondTile, direction);
        if (wiringHelper.isSource(secondTile))
            flag2 = wiringHelper.canSourceProvideTo(secondTile, direction);
        return flag1 || flag2;
    }

    public void removeTile(TileEntity tile){
        if (!isTileValid(tile))
            throw new IllegalArgumentException();
        /*PowerTile powerTile = ;
        powerTile.toGo = 3;
        pendingRemovals.add(powerTile);*/
        removeTile(getPowerTile(genCoords(tile)));
    }

    public void removeTile(T powerTile){
        if (powerTile != null) {
            for (G grid : powerTile.getGrids()) {
                if (grid != null) {
                    ElecCore.systemPrintDebug("Removing tile at " + powerTile.getLocation().toString());
                    List<BlockLoc> vec3List = new ArrayList<BlockLoc>();
                    vec3List.addAll(grid.getLocations());
                    vec3List.remove(powerTile.getLocation());
                    ElecCore.systemPrintDebug(registeredTiles.keySet().size());
                    registeredTiles.remove(powerTile.getLocation());
                    ElecCore.systemPrintDebug(registeredTiles.keySet().size());
                    ElecCore.systemPrintDebug(grids.size());
                    this.grids.remove(grid);
                    ElecCore.systemPrintDebug(grids.size());
                    List<BlockLoc> vec3List2 = new ArrayList<BlockLoc>();
                    for (BlockLoc vec : vec3List) {
                        if (!vec.equals(powerTile.getLocation())) {
                            T pt = getPowerTile(vec);
                            if (pt != null) {
                                pt.resetGrid(grid);
                                vec3List2.add(vec);
                            }
                        }
                    }
                    for (BlockLoc vec : vec3List2) {
                        ElecCore.systemPrintDebug("Re-adding tile at " + vec.toString());
                        TileEntity tileEntity1 = getTile(vec);
                        if (isTileValid(tileEntity1))
                            if (getPowerTile(vec) != null)
                                addTile(getPowerTile(vec));
                    }
                }
            }
        }
    }

    public void onServerTickInternal(){
        ElecCore.systemPrintDebug("Tick! " + world.provider.dimensionId);
        if (!pending.isEmpty() && pending.size() == oldInt) {
               /*List<PowerTile> tr = new ArrayList<PowerTile>();
               for (PowerTile powerTile : pending)
                    tr.add(powerTile);
                for (PowerTile powerTile : tr)
                    addTile(powerTile);
                pending.removeAll(tr);
                EFlux.logger.info("TickStuffPendingDone");*/
            for (T powerTile : pending){
                addTile(powerTile);
            }
            pending.clear();
        }
            /*if (!pendingRemovals.isEmpty()){
                List<PowerTile> tr = new ArrayList<PowerTile>();
                for (PowerTile powerTile : pendingRemovals){
                    powerTile.toGo--;
                    if (getTile(powerTile.getLocation()) == null){
                        powerTile.toGo = 0;
                        if (!tr.contains(powerTile))
                            removeTile(powerTile);
                    }
                    if (powerTile.toGo <= 0)
                        tr.add(powerTile);
                }
                pendingRemovals.removeAll(tr);
            }*/
        this.oldInt = pending.size();
        for (int i = 0; i < grids.size(); i++){
            try {
                grids.get(i).onTick();
            } catch (Throwable t){
                //throw new RuntimeException(t);
            }
            ElecCore.systemPrintDebug(i);
        }

    }

    public T getPowerTile(BlockLoc loc){
        return registeredTiles.get(loc);
    }

    private BlockLoc genCoords(TileEntity tileEntity){
        return new BlockLoc(tileEntity);
    }

    private TileEntity getTile(BlockLoc vec){
        return world.getTileEntity(vec.xCoord, vec.yCoord, vec.zCoord);
    }
}
