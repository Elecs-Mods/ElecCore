package elec332.core.grid.basic;

import elec332.core.main.ElecCore;
import elec332.core.util.BlockLoc;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractGridTile<G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T, W, A>, W extends AbstractWiringTypeHelper, A extends AbstractWorldGridHolder<A, G, T, W>> {
    @SuppressWarnings("unchecked")
    public AbstractGridTile(TileEntity tileEntity, W wiringHelper, A worldGridHolder){
        if (!isTileValid(tileEntity))
            throw new IllegalArgumentException();
        this.tile = tileEntity;
        this.location = new BlockLoc(tileEntity);
        this.grids = (G[]) new Object[6];
        if (wiringHelper.isTransmitter(tileEntity)) {
            this.grids[0] = newGridP(ForgeDirection.UNKNOWN);
            this.connectType = AbstractWiringTypeHelper.ConnectType.CONNECTOR;
        } else if (wiringHelper.isReceiver(tileEntity) && wiringHelper.isSource(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND_RECEIVE;
        else if (wiringHelper.isReceiver(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.RECEIVE;
        else if (wiringHelper.isSource(tileEntity))
            this.connectType = AbstractWiringTypeHelper.ConnectType.SEND;
        this.worldGridHolder =worldGridHolder;
        this.hasInit = true;
    }

    protected abstract boolean isTileValid(TileEntity tile);

    private TileEntity tile;
    private boolean hasInit = false;
    private BlockLoc location;
    private G[] grids;
    private AbstractWiringTypeHelper.ConnectType connectType;
    private final A worldGridHolder;

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
            ElecCore.systemPrintDebug("OldSizeBeforeMerge: " + q);
            int i = removeGrid(old);
            ElecCore.systemPrintDebug(i);
            grids[i] = newGrid;
            q = 0;
            for (G grid : grids){
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

    public G[] getGrids() {
        return grids;
    }

    public G getGridFromSide(ForgeDirection forgeDirection){
        return singleGrid()?getGrid():getFromSide(forgeDirection);
    }

    private G getFromSide(ForgeDirection direction){
        G grid = grids[direction.ordinal()];
        if (grid == null) {
            grid = newGridP(direction);
            grids[direction.ordinal()] = grid;
        }
        return grid;
    }

    public G getGrid(){
        if (!singleGrid())
            throw new UnsupportedOperationException("Request grid when tile has multiple grids");
        G grid = grids[0];
        if (grid == null){
            grid = newGridP(ForgeDirection.UNKNOWN);
            grids[0] = grid;
        }
        return grid;
    }

    private G newGridP(ForgeDirection direction){
        return worldGridHolder.registerGrid(newGrid(direction));
    }

    protected abstract G newGrid(ForgeDirection direction);

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass()) && location.equals(((T)obj).getLocation());
    }

}
