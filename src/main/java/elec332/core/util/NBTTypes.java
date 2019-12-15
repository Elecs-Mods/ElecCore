package elec332.core.util;

import net.minecraft.nbt.*;

/**
 * Created by Elec332 on 12-7-2015.
 */
public enum NBTTypes {

    END(new EndNBT()),                           //0
    BYTE(new ByteNBT((byte) 0)),                 //1
    SHORT(new ShortNBT((short) 0)),              //2
    INT(new IntNBT(0)),                //3
    LONG(new LongNBT(0)),              //4
    FLOAT(new FloatNBT(0)),            //5
    DOUBLE(new DoubleNBT(0)),          //6
    BYTE_ARRAY(new ByteArrayNBT(new byte[0])),   //7
    STRING(new StringNBT("")),          //8
    LIST(new ListNBT()),                         //9
    COMPOUND(new CompoundNBT()),                 //10
    INT_ARRAY(new IntArrayNBT(new int[0]));      //11

    ///##########################///

    NBTTypes(INBT nbtBase) {
        this(nbtBase.getId(), nbtBase.getClass());
    }

    NBTTypes(byte i, Class<? extends INBT> clazz) {
        this.ID = i;
        this.clazz = clazz;
        if (i != ordinal()) {
            throw new IllegalArgumentException();
        }
    }

    private final byte ID;
    private final Class<? extends INBT> clazz;

    public byte getID() {
        return ID;
    }

    public Class<? extends INBT> getClazz() {
        return clazz;
    }

    public boolean equals(int i) {
        return i == ID;
    }

    public static Class<? extends INBT> getClass(int i) {
        for (NBTTypes data : NBTTypes.values()) {
            if (data.ID == i) {
                return data.clazz;
            }
        }
        throw new IllegalArgumentException("No NBT-Type for ID: " + i);
    }

}
