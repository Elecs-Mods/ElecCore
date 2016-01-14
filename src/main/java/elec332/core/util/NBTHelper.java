package elec332.core.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;

import java.util.List;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class NBTHelper {

    public NBTHelper(){
        this(new NBTTagCompound());
    }

    public NBTHelper(NBTHelper mainTag){
        this(mainTag.tagCompound);
    }

    public NBTHelper(NBTTagCompound tagCompound){
        this.tagCompound = tagCompound;
    }

    private NBTTagCompound tagCompound;


    /*
     * Add methods
     */
    public NBTHelper addToTag(INBTSavable nbtSavable, String s){
        nbtSavable.writeToNBT(tagCompound, s);
        return this;
    }

    public NBTHelper addToTag(List<String> list, String s){
        if (list.size() > 0){
            NBTTagList tagList = new NBTTagList();
                for (String string : list){
                    tagList.appendTag(new NBTHelper().addToTag(string, s).toNBT());
                }
            tagCompound.setTag(s, tagList);
        }
        return this;
    }

    public NBTHelper addToTag(int i, String s){
        tagCompound.setInteger(s, i);
        return this;
    }

    public NBTHelper addToTag(String s, String saveName){
        tagCompound.setString(saveName, s);
        return this;
    }

    public NBTHelper addToTag(boolean b, String s){
        tagCompound.setBoolean(s, b);
        return this;
    }

    public NBTHelper addToTag(float f, String s){
        tagCompound.setFloat(s, f);
        return this;
    }

    public NBTHelper addToTag(BlockPos pos){
        return addToTag(pos, "blockLoc");
    }

    public NBTHelper addToTag(BlockPos pos, String s){
        return addToTag(new NBTHelper().addToTag(pos.getX(), "x").addToTag(pos.getY(), "y").addToTag(pos.getZ(), "z"), s);
    }

    public NBTHelper addToTag(Long l, String s){
        tagCompound.setLong(s, l);
        return this;
    }

    public NBTHelper addToTag(NBTHelper nbt, String s){
        return addToTag(nbt.tagCompound, s);
    }

    public NBTHelper addToTag(NBTBase nbtBase, String s){
        tagCompound.setTag(s, nbtBase);
        return this;
    }

    /*
     * Readers
     */

    public BlockPos getPos(){
        return getPos("blockLoc");
    }

    public BlockPos getPos(String s){
        NBTTagCompound tag = getCompoundTag(s);
        return new BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"));
    }

    public long getLong(String s){
        return tagCompound.getLong(s);
    }

    public int getInteger(String s){
        return tagCompound.getInteger(s);
    }

    public NBTTagCompound getCompoundTag(String s){
        return tagCompound.getCompoundTag(s);
    }

    public NBTTagCompound toNBT(){
        return this.tagCompound;
    }

}
