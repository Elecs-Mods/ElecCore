package elec332.core.util;

import net.minecraft.nbt.*;

/**
 * Created by Elec332 on 12-7-2015.
 */
public enum NBTTypes {

    END(EndTag.INSTANCE),                       //0
    BYTE(ByteTag.ZERO),                         //1
    SHORT(ShortTag.valueOf((short) 0)),         //2
    INT(IntTag.valueOf(0)),                     //3
    LONG(LongTag.valueOf(0)),                   //4
    FLOAT(FloatTag.valueOf(0)),                 //5
    DOUBLE(DoubleTag.valueOf(0)),               //6
    BYTE_ARRAY(new ByteArrayTag(new byte[0])),  //7
    STRING(StringTag.valueOf("")),              //8
    LIST(new ListTag()),                        //9
    COMPOUND(new CompoundTag()),                //10
    INT_ARRAY(new IntArrayTag(new int[0])),     //11
    LONG_ARRAY(new LongArrayTag(new long[0]));  //12

    ///##########################///

    NBTTypes(Tag nbtBase) {
        this(nbtBase.getId(), nbtBase.getClass());
    }

    NBTTypes(byte i, Class<? extends Tag> clazz) {
        this.ID = i;
        this.clazz = clazz;
        if (i != ordinal()) {
            throw new IllegalArgumentException();
        }
    }

    private final byte ID;
    private final Class<? extends Tag> clazz;

    public byte getID() {
        return ID;
    }

    public Class<? extends Tag> getClazz() {
        return clazz;
    }

    public boolean equals(int i) {
        return i == ID;
    }

    public static Class<? extends Tag> getClass(int i) {
        for (NBTTypes data : NBTTypes.values()) {
            if (data.ID == i) {
                return data.clazz;
            }
        }
        throw new IllegalArgumentException("No NBT-Type for ID: " + i);
    }

}
