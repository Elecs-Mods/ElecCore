package elec332.core.multiblock;

import elec332.core.baseclasses.tileentity.TileBase;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 28-7-2015.
 */
public abstract class AbstractMultiBlock extends IMultiBlock {

    public abstract boolean onAnyBlockActivated(EntityPlayer player);

    public boolean isSaveDelegate(AbstractMultiBlockTile tile){
        return tile.myLocation().equals(getLocation());
    }

    public TileBase getSaveDelegate(){
        return (TileBase) WorldHelper.getTileAt(getWorldObj(), getLocation());
    }

    public void markDirty(){
        getSaveDelegate().markDirty();
    }

    public void writeToNBT(NBTTagCompound tagCompound){
    }

    public void readFromNBT(NBTTagCompound tagCompound){
    }

}
