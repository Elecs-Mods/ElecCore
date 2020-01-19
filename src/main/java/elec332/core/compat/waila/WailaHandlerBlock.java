package elec332.core.compat.waila;

import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.handler.InformationHandler;
import elec332.core.util.math.RayTraceHelper;
import elec332.core.world.WorldHelper;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaHandlerBlock implements IComponentProvider, IServerDataProvider<TileEntity> {

    @Override
    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, TileEntity te) {
        if (data == null) {
            data = new CompoundNBT();
        }
        final BlockPos pos = te.getPos();
        final CompoundNBT tag = data;
        final BlockRayTraceResult rtr = RayTraceHelper.retraceBlock(world, pos, player);
        if (rtr == null) {
            tag.putBoolean("_nope_", true);
            return;
        }
        InformationHandler.INSTANCE.gatherInformation(tag, player, new IInfoDataAccessorBlock() {

            private BlockState ibs;

            @Nonnull
            @Override
            public PlayerEntity getPlayer() {
                return player;
            }

            @Nonnull
            @Override
            public World getWorld() {
                return world;
            }

            @Nonnull
            @Override
            public BlockPos getPos() {
                return pos;
            }

            @Nonnull
            @Override
            public CompoundNBT getData() {
                return tag;
            }

            @Nonnull
            @Override
            public Vec3d getHitVec() {
                return getRayTraceResult().getHitVec();
            }

            @Nonnull
            @Override
            public Direction getSide() {
                return getRayTraceResult().getFace();
            }

            @Nonnull
            @Override
            public BlockState getBlockState() {
                if (ibs == null) {
                    ibs = WorldHelper.getBlockState(getWorld(), getPos());
                }
                return ibs;
            }

            @Nonnull
            @Override
            public Block getBlock() {
                return getBlockState().getBlock();
            }

            @Override
            public TileEntity getTileEntity() {
                return te;
            }

            @Override
            public ItemStack getStack() {
                return null;
            }

            @Nonnull
            @Override
            public BlockRayTraceResult getRayTraceResult() {
                return rtr;
            }

        });

    }

    @Override
    public void appendBody(final List<ITextComponent> tooltip, final IDataAccessor accessor, IPluginConfig config) {
        final CompoundNBT tag = accessor.getServerData();
        if (tag != null && !tag.getBoolean("_nope_")) {
            InformationHandler.INSTANCE.addInformation(new WailaInformationType(tooltip), new IInfoDataAccessorBlock() {

                @Nonnull
                @Override
                public PlayerEntity getPlayer() {
                    return accessor.getPlayer();
                }

                @Nonnull
                @Override
                public World getWorld() {
                    return accessor.getWorld();
                }

                @Nonnull
                @Override
                public BlockPos getPos() {
                    return accessor.getPosition();
                }

                @Nonnull
                @Override
                public CompoundNBT getData() {
                    return tag;
                }

                @Nonnull
                @Override
                public Direction getSide() {
                    return accessor.getSide();
                }

                @Nonnull
                @Override
                public Vec3d getHitVec() {
                    return accessor.getHitResult().getHitVec();
                }

                @Nonnull
                @Override
                public BlockState getBlockState() {
                    return accessor.getBlockState();
                }

                @Nonnull
                @Override
                public Block getBlock() {
                    return accessor.getBlock();
                }

                @Nullable
                @Override
                public TileEntity getTileEntity() {
                    return accessor.getTileEntity();
                }

                @Override
                public ItemStack getStack() {
                    return accessor.getStack();
                }

                @Nonnull
                @Override
                public BlockRayTraceResult getRayTraceResult() {
                    return (BlockRayTraceResult) accessor.getHitResult();
                }

            });
        }
    }

}
