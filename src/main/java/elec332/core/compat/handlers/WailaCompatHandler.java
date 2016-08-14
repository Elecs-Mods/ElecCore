package elec332.core.compat.handlers;

import elec332.core.compat.ElecCoreCompatHandler;
import elec332.core.util.RayTraceHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.List;
import java.util.Map;

/**
 * Created by Elec332 on 15-8-2015.
 */
public class WailaCompatHandler implements IWailaDataProvider {

    private static final WailaCompatHandler registry = new WailaCompatHandler();
    private WailaCompatHandler(){
    }

    public static void register(IWailaRegistrar registrar){
        //registrar.registerHeadProvider(registry, IWailaInfoTile.class);
        registrar.registerBodyProvider(registry, IWailaInfoTile.class);
        //registrar.registerTailProvider(registry, IWailaInfoTile.class);
        registrar.registerNBTProvider(registry, IWailaInfoTile.class);

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
        if (accessor.getTileEntity() instanceof IWailaInfoTile){
            ((IWailaInfoTile) accessor.getTileEntity()).getWailaBody(itemStack, currentTip, accessor, config);
        }
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
        return tag;
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
            if (tile == null){
                return currenttip;
            }
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : ElecCoreCompatHandler.dataProviders.entrySet()){
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
            EnumFacing facing = RayTraceHelper.retraceBlock(world, player, pos).sideHit;
            if (te == null){
                return tag;
            }
            for (Map.Entry<Capability, IWailaCapabilityDataProvider> entry : ElecCoreCompatHandler.dataProviders.entrySet()){
                if (te.hasCapability(entry.getKey(), facing)){
                    tag = entry.getValue().getWailaTag(te.getCapability(entry.getKey(), facing), player, te, tag, world, pos);
                }
            }
            return tag;
        }

    }

    public static interface IWailaInfoTile {

        public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

        public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos);

    }

}
