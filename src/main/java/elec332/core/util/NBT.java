package elec332.core.util;

import net.minecraft.nbt.*;

/**
 * Created by Elec332 on 12-7-2015.
 */
public class NBT {

    public static Class<? extends NBTBase> getClass(int i){
        for (NBTData data : NBTData.values()){
            if (data.ID == i)
                return data.clazz;
        }
        throw new IllegalArgumentException("No NBT for ID: "+i);
    }

    public static enum NBTData{
        END((byte)0, NBTTagEnd.class),                  //0
        BYTE(new NBTTagByte((byte)0)),                  //1
        SHORT(new NBTTagShort((short)0)),               //2
        INT(new NBTTagInt(0)),                          //3
        LONG(new NBTTagLong(0)),                        //4
        FLOAT(new NBTTagFloat(0)),                      //5
        DOUBLE(new NBTTagDouble(0)),                    //6
        BYTE_ARRAY(new NBTTagByteArray(new byte[0])),   //7
        STRING(new NBTTagString("")),                   //8
        LIST(new NBTTagList()),                         //9
        COMPOUND(new NBTTagCompound()),                 //10
        INT_ARRAY(new NBTTagIntArray(new int[0]));      //11

        ///##########################///
        private NBTData(NBTBase nbtBase){
            this(nbtBase.getId(), nbtBase.getClass());
        }

        private NBTData(byte i, Class<? extends NBTBase> clazz){
            this.ID = i;
            this.clazz = clazz;
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

    }

}
