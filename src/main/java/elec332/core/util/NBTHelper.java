package elec332.core.util;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.List;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class NBTHelper {

    public NBTHelper(){
        this.tagCompound = new NBTTagCompound();
    }

    public NBTHelper(NBTTagCompound tagCompound){
        this.tagCompound = tagCompound;
    }

    public NBTHelper(NBTHelper mainTag){

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

    public NBTHelper addToTag(Long l, String s){
        tagCompound.setLong(s, l);
        return this;
    }

    public NBTHelper addToTag(NBTBase nbtBase, String s){
        tagCompound.setTag(s, nbtBase);
        return this;
    }

    public NBTTagCompound toNBT(){
        return this.tagCompound;
    }
}
