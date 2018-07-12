package elec332.core.util;

import net.minecraft.nbt.*;

/**
 * Created by Elec332 on 12-7-2015.
 */
public enum NBTTypes {

    END(new NBTTagEnd()),                           //0
    BYTE(new NBTTagByte((byte)0)),                  //1
    SHORT(new NBTTagShort((short)0)),               //2
    INT(new NBTTagInt(0)),                     //3
    LONG(new NBTTagLong(0)),                   //4
    FLOAT(new NBTTagFloat(0)),                 //5
    DOUBLE(new NBTTagDouble(0)),               //6
    BYTE_ARRAY(new NBTTagByteArray(new byte[0])),   //7
    STRING(new NBTTagString("")),              //8
    LIST(new NBTTagList()),                         //9
    COMPOUND(new NBTTagCompound()),                 //10
    INT_ARRAY(new NBTTagIntArray(new int[0]));      //11

    ///##########################///
    NBTTypes(NBTBase nbtBase){
        this(nbtBase.getId(), nbtBase.getClass());
    }

    NBTTypes(byte i, Class<? extends NBTBase> clazz){
        this.ID = i;
        this.clazz = clazz;
        if (i != ordinal()){
            throw new IllegalArgumentException();
        }
    }

    private final byte ID;
    private final Class<? extends NBTBase> clazz;

    public byte getID() {
        return ID;
    }

    public Class<? extends NBTBase> getClazz() {
        return clazz;
    }

    public boolean equals(int i){
        return i == ID;
    }

    public static Class<? extends NBTBase> getClass(int i){
        for (NBTTypes data : NBTTypes.values()){
            if (data.ID == i) {
                return data.clazz;
            }
        }
        throw new IllegalArgumentException("No NBT-Type for ID: "+i);
    }

}
