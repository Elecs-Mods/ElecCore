package elec332.core.tile.sub;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import elec332.core.api.registration.RegisteredTileEntity;
import elec332.core.tile.AbstractTileEntity;
import elec332.core.util.ItemStackHelper;
import elec332.core.util.NBTTypes;
import elec332.core.util.math.HitboxHelper;
import elec332.core.util.math.IndexedBlockPos;
import elec332.core.util.math.RayTraceHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by Elec332 on 20-2-2018
 */
@RegisteredTileEntity("tile_multi_object")
public class TileMultiObject extends AbstractTileEntity {

    @SuppressWarnings("all")
    public TileMultiObject(Class<? extends ISubTileLogic>... subtiles) {
        this();
        for (int i = 0; i < subtiles.length; i++) {
            this.subtiles.add(SubTileRegistry.INSTANCE.invoke(subtiles[i], new SubTileLogicBase.Data(this, i + 10)));
        }
    }

    @SuppressWarnings("all")
    public TileMultiObject() {
        subtiles = Lists.newArrayList();
        cachedCaps = Maps.newHashMap();
    }

    private final List<SubTileLogicBase> subtiles;
    private final Cache<List<VoxelShape>, VoxelShape> shapeMemory = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build();
    private final Map<Capability<?>, LazyOptional<?>> cachedCaps;
    private long worldTime;
    private Set<BlockPos> posSet = Sets.newHashSet();
    private Map<Integer, CompoundNBT> packetCatcher;

    public boolean shouldRefresh(long newTime, BlockPos otherPos) {
        if (worldTime != newTime) {
            posSet.clear();
            worldTime = newTime;
            posSet.add(otherPos);
            return true;
        }
        if (posSet.contains(otherPos)) {
            return false;
        }
        posSet.add(otherPos);
        return true;
    }

    public VoxelShape getShape(BlockState state, @Nonnull BlockPos pos) {
        try {
            final List<VoxelShape> shapes = subtiles.stream().map(stl -> stl.getShape(state, getData(pos))).collect(Collectors.toList());
            return shapeMemory.get(shapes, () -> HitboxHelper.combineShapes(shapes));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public VoxelShape getSelectionBox(BlockState state, PlayerEntity player, RayTraceResult last) {
        Pair<Vec3d, Vec3d> rayTraceVectors = RayTraceHelper.getRayTraceVectors(player);
        Pair<ISubTileLogic, RayTraceResult> hit = getRayTraceResult(state, rayTraceVectors.getLeft(), RayTraceHelper.slightExpand(rayTraceVectors.getLeft(), last.getHitVec()), getData(pos));
        if (hit != null) {
            return hit.getLeft().getSelectionBox(state, hit.getRight(), player);
        }
        return null;
    }

    /*
        public RayTraceResult getRayTraceResult(Vec3d start, Vec3d end, @Nonnull RayTraceResult original, @Nonnull BlockPos pos) {
            Pair<ISubTileLogic, RayTraceResult> ret = getRayTraceResult(start, RayTraceHelper.slightExpand(start, original.getHitVec()), getData(pos));
            if (ret != null) {
                return ret.getRight();
            }
            return null;
        }
    /**/
    @Nullable
    private Pair<ISubTileLogic, RayTraceResult> getRayTraceResult(BlockState state, Vec3d start, Vec3d end, int data) {
        return subtiles.stream().reduce(null, (s1, s2) -> {
            Vec3d e = end;
            if (s1 != null) {
                e = RayTraceHelper.slightExpand(start, s1.getRight().getHitVec());
            }
            VoxelShape shape = s2.getShape(state, data);
            BlockRayTraceResult hit = shape.rayTrace(start, e, getPos());
            //RayTraceResult hit = s2.getRayTraceResult(start, e, data);
            if (hit != null) {
                return Pair.of(s2, hit);
            }
            return s1;
        }, (a, b) -> a);
    }


    public void onRemoved() {
        cachedCaps.clear();
        subtiles.forEach(ISubTileLogic::onRemoved);
        subtiles.stream().filter(stl -> !stl.canBeRemoved()).forEach(ISubTileLogic::invalidate);
        cachedCaps.clear(); //You never know what sub-tile may do...
        if (!subtiles.stream().allMatch(ISubTileLogic::canBeRemoved)) {
            throw new RuntimeException();
        }
    }

    public boolean removedByPlayer(BlockState state, @Nonnull PlayerEntity player, boolean willHarvest, IFluidState fluid, @Nonnull BlockPos pos) {
        Pair<Vec3d, Vec3d> rayTraceVectors = RayTraceHelper.getRayTraceVectors(player);
        Pair<ISubTileLogic, RayTraceResult> hit = getRayTraceResult(state, rayTraceVectors.getLeft(), rayTraceVectors.getRight(), getData(pos));
        packetCatcher = Maps.newHashMap();
        if (hit != null && hit.getLeft().removedByPlayer(player, willHarvest, hit.getRight()) && subtiles.stream().allMatch(ISubTileLogic::canBeRemoved)) {
            Preconditions.checkNotNull(world).setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
            cachedCaps.clear();
            packetCatcher = null;
            return true;
        }
        packetCatcher.forEach(super::sendPacket);
        packetCatcher = null;
        return false;
    }

    public void neighborChanged(BlockPos neighborPos, boolean observer, IFluidState fluid, Block changedBlock) {
        subtiles.forEach(subTileLogicBase -> subTileLogicBase.neighborChanged(neighborPos, changedBlock, observer));
        if (subtiles.stream().allMatch(ISubTileLogic::canBeRemoved)) {
            Preconditions.checkNotNull(world).setBlockState(pos, fluid.getBlockState(), world.isRemote ? 11 : 3);
        }
    }

    @SuppressWarnings("unused")
    public boolean onBlockActivated(PlayerEntity player, Hand hand, BlockState state, BlockRayTraceResult rtr) {
        Pair<Vec3d, Vec3d> rayTraceVectors = RayTraceHelper.getRayTraceVectors(player);
        Pair<ISubTileLogic, RayTraceResult> hit = getRayTraceResult(state, rayTraceVectors.getLeft(), rayTraceVectors.getRight(), getData(pos));
        return hit != null && hit.getLeft().onBlockActivated(player, hand, hit.getRight());
    }

    public ItemStack getStack(@Nonnull RayTraceResult hit, PlayerEntity player) {
        for (SubTileLogicBase stl : subtiles) {
            ItemStack stack = stl.getStack(hit, player);
            if (stack != null) {
                return stack;
            }
        }
        return ItemStackHelper.NULL_STACK;
    }

    @Override
    public void sendPacket(int ID, CompoundNBT data) {
        if (packetCatcher != null) {
            packetCatcher.put(ID, data);
            return;
        }
        super.sendPacket(ID, data);
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT compound) {
        ListNBT stD = new ListNBT();
        subtiles.forEach(logic -> {
            ResourceLocation name = SubTileRegistry.INSTANCE.getRegistryName(logic.getClass());
            CompoundNBT tag = logic.writeToNBT(new CompoundNBT());
            tag.putString("strln", name.toString());
            stD.add(tag);
        });
        compound.put("subtiles", stD);
        return super.write(compound);
    }

    @Override
    public void read(@Nonnull CompoundNBT compound) {
        ListNBT list = compound.getList("subtiles", NBTTypes.COMPOUND.getID());
        subtiles.clear();
        for (int i = 0; i < list.size(); i++) {
            CompoundNBT tag = list.getCompound(i);
            SubTileLogicBase logic = SubTileRegistry.INSTANCE.invoke(new ResourceLocation(tag.getString("strln")), new SubTileLogicBase.Data(this, i + 10));
            logic.readFromNBT(tag);
            subtiles.add(logic);
        }
        //These 2 lines below cost me 4 hours of my life...
        cachedCaps.forEach((c, v) -> v.invalidate());
        cachedCaps.clear();
        super.read(compound);
    }

    @Override
    public void remove() {
        subtiles.forEach(SubTileLogicBase::invalidate);
        cachedCaps.forEach((c, v) -> v.invalidate());
        cachedCaps.clear();
    }

    @Override
    public void onLoad() {
        subtiles.forEach(SubTileLogicBase::onLoad);
    }

    @Override
    public void sendInitialLoadPackets() {
        subtiles.forEach(SubTileLogicBase::sendInitialLoadPackets);
    }

    @Override
    public void onDataPacket(int id, CompoundNBT tag) {
        if (id >= 10) { //First 10 ID's are reserved for internal use
            SubTileLogicBase st = subtiles.get(id - 10);
            st.onDataPacket(tag.getInt("kid"), tag.getCompound("data"));
            return;
        }
        super.onDataPacket(id, tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        boolean c = SubTileRegistry.INSTANCE.isCacheable(capability);
        LazyOptional<?> ret;
        if (c) {
            ret = cachedCaps.get(capability);
            if (ret != null && ret.isPresent()) {
                return ret.cast();
            } else {
                cachedCaps.remove(capability);
            }
        }
        List<LazyOptional<T>> capabilities = Lists.newArrayList(super.getCapability(capability, facing));
        subtiles.forEach(s -> capabilities.add(s.getCapability(capability, facing)));
        ret = SubTileRegistry.INSTANCE.getCombined(capability, capabilities);
        if (c) {
            cachedCaps.put(capability, ret);
        }
        return ret.cast();
    }

    private static int getData(BlockPos pos) {
        return pos instanceof IndexedBlockPos ? ((IndexedBlockPos) pos).index : 0;
    }

}
