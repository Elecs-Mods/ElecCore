package elec332.core.compat.top;

import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.handler.InformationHandler;
import elec332.core.util.math.RayTraceHelper;
import elec332.core.world.WorldHelper;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 19-1-2020
 */
public class TOPHandlerBlock implements IProbeInfoProvider {

    @Override
    public String getID() {
        return "eleccore:multi_info";
    }

    @Override
    public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData hitData) {
        final BlockRayTraceResult rtr = RayTraceHelper.retraceBlock(world, hitData.getPos(), playerEntity);
        if (rtr == null) {
            return;
        }
        final CompoundNBT tag = new CompoundNBT();
        final TileEntity tile = WorldHelper.getTileAt(world, hitData.getPos());
        IInfoDataAccessorBlock infoAccessor = new IInfoDataAccessorBlock() {

            @Nonnull
            @Override
            public PlayerEntity getPlayer() {
                return playerEntity;
            }

            @Nonnull
            @Override
            public World getWorld() {
                return world;
            }

            @Nonnull
            @Override
            public BlockPos getPos() {
                return hitData.getPos();
            }

            @Nonnull
            @Override
            public CompoundNBT getData() {
                return tag;
            }

            @Nonnull
            @Override
            public Direction getSide() {
                return hitData.getSideHit();
            }

            @Nonnull
            @Override
            public Vector3d getHitVec() {
                return hitData.getHitVec();
            }

            @Nonnull
            @Override
            public BlockState getBlockState() {
                return blockState;
            }

            @Nonnull
            @Override
            public Block getBlock() {
                return blockState.getBlock();
            }

            @Nullable
            @Override
            public TileEntity getTileEntity() {
                return tile;
            }

            @Override
            public ItemStack getStack() {
                return hitData.getPickBlock();
            }

            @Nonnull
            @Override
            public BlockRayTraceResult getRayTraceResult() {
                return rtr;
            }

        };

        InformationHandler.INSTANCE.gatherInformation(tag, (ServerPlayerEntity) playerEntity, infoAccessor);
        InformationHandler.INSTANCE.addInformation(new TOPInformationType(probeMode, iProbeInfo), infoAccessor);
    }

}
