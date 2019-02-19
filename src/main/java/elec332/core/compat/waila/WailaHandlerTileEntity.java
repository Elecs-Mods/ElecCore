package elec332.core.compat.waila;

import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.handler.InformationHandler;
import elec332.core.util.RayTraceHelper;
import elec332.core.world.WorldHelper;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaHandlerTileEntity implements IComponentProvider, IServerDataProvider<TileEntity> {

    @Override
    public void appendServerData(NBTTagCompound data, EntityPlayerMP player, World world, TileEntity te) {
        if (data == null) {
            data = new NBTTagCompound();
        }
        final BlockPos pos = te.getPos();
        final NBTTagCompound tag = data;
        final RayTraceResult rtr = RayTraceHelper.retraceBlock(world, pos, player);
        if (rtr == null) {
            tag.putBoolean("_nope_", true);
            return;
        }
        InformationHandler.INSTANCE.getInfoNBTData(tag, te, player, new IInfoDataAccessorBlock() {

            private IBlockState ibs;

            @Nonnull
            @Override
            public EntityPlayer getPlayer() {
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
            public NBTTagCompound getData() {
                return tag;
            }

            @Override
            public Vec3d getHitVec() {
                return getRayTraceResult().hitVec;
            }

            @Nonnull
            @Override
            public EnumFacing getSide() {
                return getRayTraceResult().sideHit;
            }

            @Nonnull
            @Override
            public IBlockState getBlockState() {
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

            @Override
            public RayTraceResult getRayTraceResult() {
                return rtr;
            }

        });

    }

    @Override
    public void appendBody(final List<ITextComponent> tooltip, final IDataAccessor accessor, IPluginConfig config) {
        final NBTTagCompound tag = accessor.getServerData();
        if (tag != null && !tag.getBoolean("_nope_")) {
            InformationHandler.INSTANCE.addInformation(new WailaInformationType(tooltip), new IInfoDataAccessorBlock() {

                @Nonnull
                @Override
                public EntityPlayer getPlayer() {
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
                public NBTTagCompound getData() {
                    return tag;
                }

                @Nonnull
                @Override
                public EnumFacing getSide() {
                    return accessor.getSide();
                }

                @Override
                public Vec3d getHitVec() {
                    return accessor.getHitResult() == null ? null : accessor.getHitResult().hitVec;
                }

                @Nonnull
                @Override
                public IBlockState getBlockState() {
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

                @Override
                public RayTraceResult getRayTraceResult() {
                    return accessor.getHitResult();
                }

            });
        }
    }

}
