package elec332.core.util;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 28-5-2015.
 */
public interface INBTSavable {

    public void saveToNBT(NBTTagCompound tagCompound, String s);

    public void writeToNBT(NBTTagCompound tagCompound, String s);

}
