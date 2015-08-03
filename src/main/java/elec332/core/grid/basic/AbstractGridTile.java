package elec332.core.grid.basic;

import elec332.core.util.BlockLoc;
import elec332.eflux.EFlux;
import elec332.eflux.api.energy.IEnergyReceiver;
import elec332.eflux.api.energy.IEnergySource;
import elec332.eflux.api.energy.IEnergyTransmitter;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractGridTile<G extends AbstractCableGrid, T extends AbstractGridTile> {
    @SuppressWarnings("unchecked")
    public AbstractGridTile(TileEntity tileEntity){
        if (!isTileValid(tileEntity))
            throw new IllegalArgumentException();
        this.tile = tileEntity;
        this.location = new BlockLoc(tileEntity);
        this.grids = (G[]) new Object[6];
        if (tileEntity instanceof IEnergyTransmitter) {
            this.grids[0] = newGrid(ForgeDirection.UNKNOWN);
            this.connectType = AbstractWiringTypeHelper.ConnectType.CONNECTOR;
        } else if (tileEntity instanceof IEnergyReceiver && tileEntity instanceof IEnergySource)
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND_RECEIVE;
        else if (tileEntity instanceof IEnergyReceiver)
            this.connectType = AbstractWiringTypeHelper.ConnectType.RECEIVE;
        else if (tileEntity instanceof IEnergySource)
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND;
        this.hasInit = true;
    }

    protected abstract boolean isTileValid(TileEntity tile);

    private TileEntity tile;
    private boolean hasInit = false;
    private BlockLoc location;
    private G[] grids;
    public int toGo;
    private AbstractWiringTypeHelper.ConnectType connectType;

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
            for (G grid : grids){
                if (grid != null)
                    q++;
            }
            EFlux.systemPrintDebug("OldSizeBeforeMerge: " + q);
            int i = removeGrid(old);
            EFlux.systemPrintDebug(i);
            grids[i] = newGrid;
            q = 0;
            for (G grid : grids){
                if (grid != null)
                    q++;
            }
            EFlux.systemPrintDebug("NewSizeAfterMerge " + q);
            EFlux.systemPrintDebug(grids.length);
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

    public G[] getGrids() {
        return grids;
    }

    public G getGridFromSide(ForgeDirection forgeDirection){
        return singleGrid()?getGrid():getFromSide(forgeDirection);
    }

    private G getFromSide(ForgeDirection direction){
        G grid = grids[direction.ordinal()];
        if (grid == null) {
            grid = newGrid(direction);
            grids[direction.ordinal()] = grid;
        }
        return grid;
    }

    public G getGrid(){
        if (!singleGrid())
            throw new UnsupportedOperationException("Request grid when tile has multiple grids");
        G grid = grids[0];
        if (grid == null){
            grid = newGrid(ForgeDirection.UNKNOWN);
            grids[0] = grid;
        }
        return grid;
    }

    protected abstract G newGrid(ForgeDirection direction);

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) && location.equals(((T)obj).getLocation());
    }

}
