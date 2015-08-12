package elec332.core.grid.basic;

import elec332.core.main.ElecCore;
import elec332.core.registry.AbstractWorldRegistryHolder;
import elec332.core.util.BlockLoc;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Elec332 on 3-8-2015.
 */
public abstract class AbstractCableGrid<G extends AbstractCableGrid<G, T, W, A>, T extends AbstractGridTile<G, T, W, A>, W extends AbstractWiringTypeHelper, A extends AbstractWorldGridHolder<A, G, T, W>>{
    public AbstractCableGrid(World world, T p, ForgeDirection direction, W wiringHelper, AbstractWorldRegistryHolder<A> worldGridHolder){
        acceptors = new ArrayList<GridData>();
        providers = new ArrayList<GridData>();
        locations = new ArrayList<BlockLoc>();
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
        //if (wiringHelper.isTransmitter(p.getTile()))
        //FMLCommonHandler.instance().bus().register(this);
        identifier = UUID.randomUUID();
        this.worldGridHolder = worldGridHolder;
    }

    protected final UUID identifier;
    protected final World world;
    protected List<GridData> acceptors;
    protected List<GridData> providers;
    protected List<GridData> specialProviders;
    protected List<GridData> transmitters;
    protected List<BlockLoc> locations;
    private final AbstractWorldRegistryHolder<A> worldGridHolder;

    public List<BlockLoc> getLocations(){
        return locations;
    }

    @SuppressWarnings("unchecked")
    public final G mergeGrids(G grid){
        if (this.world.provider.dimensionId != grid.world.provider.dimensionId)
            throw new RuntimeException();
        if (this.equals(grid))
            return (G)this;
        uponGridMerge(grid);
        for (BlockLoc vec : grid.locations){
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
        public GridData(BlockLoc blockLoc, ForgeDirection direction){
            this.loc = blockLoc;
            this.direction = direction;
        }

        private BlockLoc loc;
        private ForgeDirection direction;

        public BlockLoc getLoc() {
            return loc;
        }

        public ForgeDirection getDirection() {
            return direction;
        }

        @Override
        public int hashCode() {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof GridData && ((GridData) obj).loc.equals(loc) && ((GridData) obj).direction.equals(direction);
        }
    }
}
