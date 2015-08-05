package elec332.core.grid.basic;

import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.util.BlockLoc;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractGridTile<G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T, W, A>, W extends AbstractWiringTypeHelper, A extends AbstractWorldGridHolder<A, G, T, W>> {
    @SuppressWarnings("unchecked")
    public AbstractGridTile(TileEntity tileEntity, W wiringHelper, AbstractWorldRegistryHolder<A> worldGridHolder){
        if (!wiringHelper.isTileValid(tileEntity))
            throw new IllegalArgumentException();
        this.tile = tileEntity;
        this.location = new BlockLoc(tileEntity);
        this.grids = new Object[6];
        this.worldGridHolder =worldGridHolder;
        if (wiringHelper.isTransmitter(tileEntity)) {
            this.grids[0] = newGridP(ForgeDirection.UNKNOWN);
            this.connectType = AbstractWiringTypeHelper.ConnectType.CONNECTOR;
        } else if (wiringHelper.isReceiver(tileEntity) && wiringHelper.isSource(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND_RECEIVE;
        else if (wiringHelper.isReceiver(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.RECEIVE;
        else if (wiringHelper.isSource(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND;
        this.hasInit = true;
    }

    private TileEntity tile;
    private boolean hasInit = false;
    private BlockLoc location;
    private Object[] grids;
    private AbstractWiringTypeHelper.ConnectType connectType;
    private final AbstractWorldRegistryHolder<A> worldGridHolder;

    @SuppressWarnings("unchecked")
    private G getGrid(int i){
        return (G) grids[i];
    }

    private boolean singleGrid(){
        return this.connectType == AbstractWiringTypeHelper.ConnectType.CONNECTOR;
    }

    public AbstractWiringTypeHelper.ConnectType getConnectType() {
        return connectType;
    }

    public BlockLoc getLocation() {
        return location;
    }

    public TileEntity getTile() {
        return tile;
    }

    public boolean hasInit() {
        return hasInit;
    }

    public void replaceGrid(G old, G newGrid){
        if (singleGrid()){
            grids[0] = newGrid;
        } else {
            int q = 0;
            for (Object grid : grids){
                if (grid != null)
                    q++;
            }
            ElecCore.systemPrintDebug("OldSizeBeforeMerge: " + q);
            int i = removeGrid(old);
            ElecCore.systemPrintDebug(i);
            grids[i] = newGrid;
            q = 0;
            for (Object grid : grids){
                if (grid != null)
                    q++;
            }
            ElecCore.systemPrintDebug("NewSizeAfterMerge " + q);
            ElecCore.systemPrintDebug(grids.length);
        }
    }

    public void resetGrid(G grid){
        removeGrid(grid);
        if (singleGrid())
            getGrid();
    }

    public int removeGrid(G grid){
        if (grids.length == 0)
            throw new RuntimeException();
        for (int i = 0; i < grids.length; i++){
            if (grid.equals(grids[i])){
                grids[i] = null;
                return i;
            }
        }
        return -1;
    }

    @SuppressWarnings("unchecked")
    public List<G> getGrids() {
        List<G> ret = Lists.newArrayList();
        for (int i = 0; i < grids.length; i++) {
            ret.add(getGrid(i));
        }
        return ret;
    }

    public G getGridFromSide(ForgeDirection forgeDirection){
        return singleGrid()?getGrid():getFromSide(forgeDirection);
    }

    private G getFromSide(ForgeDirection direction){
        G grid = getGrid(direction.ordinal());
        if (grid == null) {
            grid = newGridP(direction);
            grids[direction.ordinal()] = grid;
        }
        return grid;
    }

    public G getGrid(){
        if (!singleGrid())
            throw new UnsupportedOperationException("Request grid when tile has multiple grids");
        G grid = getGrid(0);
        if (grid == null){
            grid = newGridP(ForgeDirection.UNKNOWN);
            grids[0] = grid;
        }
        return grid;
    }

    protected AbstractWorldRegistryHolder<A> getWorldGridHolder() {
        return this.worldGridHolder;
    }

    private G newGridP(ForgeDirection direction){
        return worldGridHolder.get(tile.getWorldObj()).registerGrid(newGrid(direction));
    }

    protected abstract G newGrid(ForgeDirection direction);

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) && location.equals(((T)obj).getLocation());
    }

}
