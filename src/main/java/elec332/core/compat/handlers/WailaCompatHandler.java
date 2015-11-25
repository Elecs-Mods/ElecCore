package elec332.core.compat.handlers;

import mcp.mobius.waila.api.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
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
    public ITaggedList.ITipList getWailaHead(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public ITaggedList.ITipList getWailaBody(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        if (accessor.getTileEntity() instanceof IWailaInfoTile){
            ((IWailaInfoTile) accessor.getTileEntity()).getWailaBody(itemStack, currentTip, accessor, config);
        }
        return currentTip;
    }

    @Override
    public ITaggedList.ITipList getWailaTail(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getNBTData(TileEntity te, NBTTagCompound tag, IWailaDataAccessorServer accessor) {
        if (te instanceof IWailaInfoTile && tag != null){
            return ((IWailaInfoTile) te).getWailaTag(te, tag, accessor);
        }
        return tag;
    }

    public static interface IWailaInfoTile {

        public ITaggedList.ITipList getWailaBody(ItemStack itemStack, ITaggedList.ITipList currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config);

        public NBTTagCompound getWailaTag(TileEntity tile, NBTTagCompound tag, IWailaDataAccessorServer accessor);

    }

}
