package elec332.core.grid.basic;

import elec332.core.main.ElecCore;
import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.world.WorldHelper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractCableGrid<G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T, W, A>, W extends IWiringTypeHelper, A extends AbstractWorldGridHolder<A, G, T, W>>{

    public AbstractCableGrid(World world, T p, EnumFacing direction, W wiringHelper, AbstractWorldRegistryHolder<A> worldGridHolder){
        acceptors = new ArrayList<GridData>();
        providers = new ArrayList<GridData>();
        locations = new ArrayList<BlockPos>();
        specialProviders = new ArrayList<GridData>();
        transmitters = new ArrayList<GridData>();
        this.world = world;
        locations.add(p.getLocation());
        if (wiringHelper.isTransmitter(p.getTile()))
            transmitters.add(new GridData(p.getLocation(), direction));
        if (wiringHelper.isSource(p.getTile()) && wiringHelper.canSourceProvideTo(p.getTile(), direction)) {
            providers.add(new GridData(p.getLocation(), direction));
        }
        if (wiringHelper.isReceiver(p.getTile()) && wiringHelper.canReceiverReceiveFrom(p.getTile(), direction))
            acceptors.add(new GridData(p.getLocation(), direction));
        identifier = UUID.randomUUID();
        this.worldGridHolder = worldGridHolder;
    }

    protected final UUID identifier;
    protected final World world;
    protected List<GridData> acceptors;
    protected List<GridData> providers;
    protected List<GridData> specialProviders;
    protected List<GridData> transmitters;
    protected List<BlockPos> locations;
    private final AbstractWorldRegistryHolder<A> worldGridHolder;

    public List<BlockPos> getLocations(){
        return locations;
    }

    @SuppressWarnings("unchecked")
    public final G mergeGrids(G grid){
        if (WorldHelper.getDimID(world) != WorldHelper.getDimID(grid.world))
            throw new RuntimeException();
        if (this.equals(grid))
            return (G)this;
        uponGridMerge(grid);
        for (BlockPos vec : grid.locations){
            T powerTile = getWorldHolder().getPowerTile(vec);
            if (powerTile != null)
                powerTile.replaceGrid(grid, (G)this);
        }
        getWorldHolder().removeGrid(grid);
        ElecCore.systemPrintDebug("MERGED");
        return (G)this;
    }

    /**
     * Use this to copy data over from the parameter, the parameter is
     * a grid that will be merged into this one
     *
     * @param grid The grid that will be removed
     */
    protected void uponGridMerge(G grid){
        this.locations.addAll(grid.locations);
        this.acceptors.addAll(grid.acceptors);
        this.providers.addAll(grid.providers);
        this.transmitters.addAll(grid.transmitters);
        this.specialProviders.addAll(grid.specialProviders);
    }

    /**
     * This gets called right before a this grid gets recreated because
     * a tile gor removed from the grid.
     *
     * @param tile The tile that will be removed
     */
    protected void onTileRemoved(T tile){
    }

    public abstract void onTick();

    protected A getWorldHolder(){
        return this.worldGridHolder.get(world);
    }

    protected void invalidate(){
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object obj) {
        return obj != null && obj.getClass().equals(getClass()) && ((G)obj).identifier.equals(identifier);
    }

    public static class GridData {
        public GridData(BlockPos blockLoc, EnumFacing direction){
            this.loc = blockLoc;
            this.direction = direction;
        }

        private BlockPos loc;
        private EnumFacing direction;

        public BlockPos getLoc() {
            return loc;
        }

        public EnumFacing getDirection() {
            return direction;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GridData && ((GridData) obj).loc.equals(loc) && ((GridData) obj).direction == (direction);
        }
    }
}
