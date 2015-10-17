package elec332.core.multiblock;

import elec332.core.baseclasses.tileentity.IInventoryTile;
import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.compat.handlers.WailaCompatHandler;
import elec332.core.util.BlockLoc;
import elec332.core.world.WorldHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 28-7-2015.
 */
public abstract class AbstractMultiBlock extends IMultiBlock implements IInventoryTile, WailaCompatHandler.IWailaInfoTile{

    public abstract boolean onAnyBlockActivated(EntityPlayer player);

    public boolean isSaveDelegate(AbstractMultiBlockTile tile){
        return tile.myLocation().equals(getLocation());
    }

    public TileBase getSaveDelegate(){
        return (TileBase) getTileAt(getLocation());
    }

    public TileEntity getTileAt(BlockLoc loc){
        return WorldHelper.getTileAt(getWorldObj(), loc);
    }

    public boolean isServer(){
        return !getWorldObj().isRemote;
    }

    public BlockLoc getBlockLocAtTranslatedPos(int length, int width, int height){
        return MultiBlockStructureRegistry.getTranslated(getLocation().xCoord, getLocation().yCoord, getLocation().zCoord, getMultiBlockFacing(), length, width, height);
    }

    public TileEntity getTileAtTranslatedPos(int length, int width, int height){
        return getTileAt(getBlockLocAtTranslatedPos(length, width, height));
    }

    public void markDirty(){
        getSaveDelegate().markDirty();
    }

    public void writeToNBT(NBTTagCompound tagCompound){
    }

    public void readFromNBT(NBTTagCompound tagCompound){
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currentTip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return currentTip;
    }

    @Override
    public NBTTagCompound getWailaTag(EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, int x, int y, int z){
        return tag;
    }


    public final boolean openGui(EntityPlayer player, Object mod, int ID){
        player.openGui(mod, ID, getWorldObj(), getLocation().xCoord, getLocation().yCoord, getLocation().zCoord);
        return true;
    }

    public final boolean openGui(EntityPlayer player, Object mod){
        return openGui(player, mod, 0);
    }

    @Override
    public final Container getGuiServer(EntityPlayer player){
        return (Container) getGui(player, false);
    }

    @Override
    public final Object getGuiClient(EntityPlayer player) {
        return getGui(player, true);
    }

    public Object getGui(EntityPlayer player, boolean client){
        return null;
    }
}
