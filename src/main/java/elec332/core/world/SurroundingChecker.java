package elec332.core.world;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by Elec332 on 5-3-2016.
 */
public class SurroundingChecker<O> {

    public SurroundingChecker(IFacedSurroundingDataFactory<O> factory, World world, ISurroundingHandler<O> surroundingHandler){
        this.surroundings = new PositionedObjectHolder<SurroundingData>();
        this.surroundingDataFactory = factory;
        this.surroundingHandler = surroundingHandler;
        this.world = world;
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final World world;
    private final PositionedObjectHolder<SurroundingData> surroundings;
    private final IFacedSurroundingDataFactory<O> surroundingDataFactory;
    private final ISurroundingHandler<O> surroundingHandler;

    public void addPosition(BlockPos pos){
        if (surroundings.get(pos) != null){
            throw new IllegalStateException();
        }
        resetPositionData(pos);
    }

    public void resetPositionData(BlockPos pos){
        surroundings.put(new SurroundingData(pos), pos);
    }

    public void removePosition(BlockPos pos){
        surroundings.remove(pos);
    }

    public O getSurroundingData(BlockPos pos, EnumFacing side){
        return surroundings.get(pos).data.get(side).getRight();
    }

    @SubscribeEvent
    public void onNeighborChangeEvent(BlockEvent.NeighborNotifyEvent event){
        if (WorldHelper.getDimID(world) == WorldHelper.getDimID(event.getWorld())){
            for (EnumFacing facing : EnumFacing.VALUES){
                BlockPos pos = event.getPos().offset(facing);
                checkForPos(pos);
            }
            checkForPos(event.getPos());
        }
    }

    @SuppressWarnings("all")
    public boolean isChange(BlockPos pos) {
        SurroundingData oldData = surroundings.get(pos);
        SurroundingData newData = new SurroundingData(pos);
        boolean ret = oldData == null || newData.equalCheck(oldData, false);
        for (EnumFacing facing : EnumFacing.VALUES){
            if (ret){
                return ret;
            }
            BlockPos pos_ = pos.offset(facing);
            SurroundingData s = surroundings.get(pos_);
            if (s != null){
                ret |= new SurroundingData(pos).equalCheck(s, false);
            }
        }
        return ret;
    }

    private void checkForPos(BlockPos pos){
        SurroundingData oldData = surroundings.get(pos);
        if (oldData != null){
            SurroundingData newData = new SurroundingData(pos);
            newData.equalCheck(oldData, true);
            surroundings.put(newData, pos);
        }
    }

    public interface ISurroundingHandler<O> {

        public void onSurroundingsChanged(BlockPos pos, EnumFacing side, @Nullable O oldSurroundings, @Nullable O newSurroundings, boolean blockChange);

    }

    public interface IFacedSurroundingDataFactory<O> {

        /**
         * Generates surrounding data for the given position
         *
         * @param pos The position of which the surroundings hav to be checked
         * @param world The world
         * @param facing The facing that has to be checked
         * @return The generated data about the surroundings
         */
        public O generateFor(World world, BlockPos pos, EnumFacing facing);

        public boolean equal(O o1, O o2);

    }

    private class SurroundingData {

        private SurroundingData(BlockPos pos){
            this.pos = pos;
            data = Maps.newEnumMap(EnumFacing.class);
            for (EnumFacing facing : EnumFacing.VALUES){
                O s = surroundingDataFactory.generateFor(world, pos, facing);
                data.put(facing, Pair.of(WorldHelper.getBlockAt(world, pos.offset(facing)), s));
                hash += facing.ordinal() * s.hashCode();
            }
        }

        private final BlockPos pos;
        private final Map<EnumFacing, Pair<Block, O>> data;
        private int hash;

        @Override
        public int hashCode() {
            return hash;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object obj) {
            return obj.getClass() == getClass() && equalCheck((SurroundingData) obj, false);
        }

        private boolean equalCheck(SurroundingData oldSurroundingData, boolean notify){
            boolean ret = true;
            for (EnumFacing facing : EnumFacing.VALUES){
                Pair<Block, O> oldO = oldSurroundingData.data.get(facing);
                Pair<Block, O> newO = data.get(facing);
                if (!surroundingDataFactory.equal(newO.getRight(), oldO.getRight())){
                    if (notify) {
                        ret = false;
                        surroundingHandler.onSurroundingsChanged(pos, facing, oldO.getRight(), newO.getRight(), oldO.getLeft() != newO.getLeft());
                    } else {
                        return false;
                    }
                }
            }
            return ret;
        }

    }

}
