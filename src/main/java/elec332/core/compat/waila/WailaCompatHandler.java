package elec332.core.compat.waila;

import elec332.core.api.info.IInfoDataAccessorBlock;
import elec332.core.api.info.IInformation;
import elec332.core.api.info.InfoMod;
import elec332.core.api.module.ElecModule;
import elec332.core.handler.InformationHandler;
import elec332.core.main.ElecCore;
import elec332.core.main.ElecCoreRegistrar;
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
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 15-8-2015.
 */
@ElecModule(owner = ElecCore.MODID, name = "WailaCompat", modDependencies = "Waila")
public class WailaCompatHandler implements IWailaDataProvider {

    @ElecModule.Instance
    private static WailaCompatHandler instance;

    private static Map<Capability, IWailaCapabilityDataProvider> map;

    @ElecModule.EventHandler
    public void init(FMLInitializationEvent event){
        FMLInterModComms.sendMessage("Waila", "register", getClass().getCanonicalName()+".register");
    }

    public static void register(IWailaRegistrar registrar){
        registrar.registerBodyProvider(instance, Block.class);
        registrar.registerNBTProvider(instance, Block.class);

        CapabilityProvider capabilityProvider = new CapabilityProvider();
        registrar.registerBodyProvider(capabilityProvider, TileEntity.class);
        registrar.registerNBTProvider(capabilityProvider, TileEntity.class);
    }

    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        TileEntity tile = accessor.getTileEntity();
        if (tile instanceof IWailaInfoTile){
            ((IWailaInfoTile) accessor.getTileEntity()).getWailaBody(itemStack, currentTip, accessor, config);
        }
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
        return currentTip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
        if (te instanceof IWailaInfoTile && tag != null){
            return ((IWailaInfoTile) te).getWailaTag(player, te, tag, world, pos);
        }
        if (tag == null){
            tag = new NBTTagCompound();
        }
        final NBTTagCompound fTag = tag;
        return InformationHandler.INSTANCE.getInfoNBTData(fTag, te, player, new IInfoDataAccessorBlock() {

            private RayTraceResult rtr;
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
                if (ibs == null){
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
                if (rtr == null){
                    rtr = RayTraceHelper.retraceBlock(world, player, pos);
                }
                return rtr;
            }

        });
    }

    @SuppressWarnings("unchecked")
    private static class CapabilityProvider implements IWailaDataProvider {

        @Override
        public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            EnumFacing facing = accessor.getSide();
            TileEntity tile = accessor.getTileEntity();
            if (tile == null || accessor.getNBTData().getBoolean("nope")){
                return currenttip;
            }
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : map.entrySet()){
                if (tile.hasCapability(entry.getKey(), facing)){
                    currenttip = entry.getValue().getWailaBody(currenttip, tile.getCapability(entry.getKey(), facing), accessor.getNBTData(), accessor.getPlayer(), accessor.getMOP(), accessor.getWorld(), accessor.getPosition(), accessor.getTileEntity());
                }
            }
            return currenttip;
        }

        @Override
        public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
            return null;
        }

        @Override
        public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
            RayTraceResult rtr = RayTraceHelper.retraceBlock(world, player, pos);
            if (te == null || rtr == null){
                tag.setBoolean("nope", true);
                return tag;
            }
            EnumFacing facing = rtr.sideHit;
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : map.entrySet()){
                if (te.hasCapability(entry.getKey(), facing)){
                    tag = entry.getValue().getWailaTag(te.getCapability(entry.getKey(), facing), player, te, tag, world, pos);
                }
            }
            return tag;
        }

    }

    @Deprecated
    public static interface IWailaInfoTile {

        public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

        public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos);

    }

    static {
        map = ElecCoreRegistrar.WAILA_CAPABILITY_PROVIDER.getAllRegisteredObjects();
    }

}
