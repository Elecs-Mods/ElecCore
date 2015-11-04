package elec332.core.compat.handlers;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

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
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y, int z) {
        if (te instanceof IWailaInfoTile && tag != null){
            return ((IWailaInfoTile) te).getWailaTag(player, te, tag, world, x, y, z);
        }
        return tag;
    }

    public static interface IWailaInfoTile {

        public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

        public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z);

    }

}
