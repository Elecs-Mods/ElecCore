package elec332.core.multiblock.dynamic;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Elec332 on 9-8-2015.
 */
public interface IDynamicMultiBlockTile<M extends AbstractDynamicMultiBlock> {

    public void setMultiBlock(M multiBlock);

    public M getMultiBlock();

    public void setSaveData(NBTTagCompound tag);

    public NBTTagCompound getSaveData();

}
