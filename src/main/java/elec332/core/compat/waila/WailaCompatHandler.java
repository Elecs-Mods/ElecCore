package elec332.core.compat.waila;

import elec332.core.ElecCore;
import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import elec332.core.api.module.ElecModule;
import elec332.core.compat.ModNames;
import elec332.core.handler.InformationHandler;
import elec332.core.util.RayTraceHelper;
import elec332.core.world.WorldHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
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
import net.minecraft.world.World;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 15-8-2015.
 */
@SuppressWarnings("deprecation")
@ElecModule(owner = ElecCore.MODID, name = "WailaCompat", modDependencies = ModNames.WAILA)
public class WailaCompatHandler implements IWailaDataProvider {

    @ElecModule.Instance
    private static WailaCompatHandler instance;

    @ElecModule.EventHandler
    public void init(InterModEnqueueEvent event) {
        InterModComms.sendTo(ModNames.WAILA, "register", () -> getClass().getCanonicalName() + ".register");
    }

    public static void register(IWailaRegistrar registrar) {
        registrar.registerBodyProvider(instance, Block.class);
        registrar.registerNBTProvider(instance, Block.class);
    }

    @Nonnull
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return accessor.getStack();
    }

    @Nonnull
    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Nonnull
    @Override
    public List<String> getWailaBody(ItemStack itemStack, final List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getNBTData() != null && !accessor.getNBTData().getBoolean("_nope_")) {
            InformationHandler.INSTANCE.addInformation(new IInformation() {

                @Nonnull
                @Override
                public InfoMod getProviderType() {
                    return InfoMod.WAILA;
                }

                @Override
                public void addInformation(String line) {
                    currentTip.add(line);
                }

            }, new IInfoDataAccessorBlock() {

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
                    return accessor.getNBTData();
                }

                @Nonnull
                @Override
                public EnumFacing getSide() {
                    return accessor.getSide();
                }

                @Override
                public Vec3d getHitVec() {
                    return accessor.getMOP() == null ? null : accessor.getMOP().hitVec;
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
                    return accessor.getMOP();
                }

            });
        }
        return currentTip;
    }

    @Nonnull
    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Nonnull
    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (tag == null) {
            tag = new NBTTagCompound();
        }
        final NBTTagCompound fTag = tag;
        RayTraceResult rtr = RayTraceHelper.retraceBlock(world, pos, player);
        if (rtr == null) {
            fTag.putBoolean("_nope_", true);
            return fTag;
        }
        final RayTraceResult rtrF = rtr;
        return InformationHandler.INSTANCE.getInfoNBTData(fTag, te, player, new IInfoDataAccessorBlock() {

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
                return fTag;
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
                return rtrF;
            }

        });
    }

}