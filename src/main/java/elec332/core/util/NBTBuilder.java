package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 27-7-2018
 * <p>
 * Builder for {@link NBTTagCompound}
 * Can be used to chain writes to a {@link NBTTagCompound}
 */
public class NBTBuilder implements INBTSerializable<NBTTagCompound>, Supplier<NBTTagCompound> {

    public static NBTBuilder from(NBTTagCompound tag) {
        return new NBTBuilder(tag);
    }

    public NBTBuilder() {
        this(new NBTTagCompound());
    }

    public NBTBuilder(NBTTagCompound tag) {
        this.tag = Preconditions.checkNotNull(tag);
    }

    private NBTTagCompound tag;

    /////////////////////////////

    public NBTBuilder setBlockPos(BlockPos pos) {
        return setBlockPos("position", pos);
    }

    public NBTBuilder setBlockPos(String name, BlockPos pos) {
        this.tag.setLong(name, pos.toLong());
        return this;
    }

    public NBTBuilder setTag(String name, INBTSerializable<?> tag) {
        return setTag(name, tag.serializeNBT());
    }

    public NBTBuilder setTag(String name, NBTBase tag) {
        this.tag.setTag(name, tag);
        return this;
    }

    public NBTBuilder setInteger(String name, int i) {
        this.tag.setInteger(name, i);
        return this;
    }

    public NBTBuilder setByte(String name, byte b) {
        this.tag.setByte(name, b);
        return this;
    }

    public NBTBuilder setShort(String name, short s) {
        this.tag.setShort(name, s);
        return this;
    }

    public NBTBuilder setLong(String name, long l) {
        this.tag.setLong(name, l);
        return this;
    }

    public NBTBuilder setFloat(String name, float f) {
        this.tag.setFloat(name, f);
        return this;
    }

    public NBTBuilder setDouble(String name, double d) {
        this.tag.setDouble(name, d);
        return this;
    }

    public NBTBuilder setString(String name, String str) {
        this.tag.setString(name, str);
        return this;
    }

    public NBTBuilder setByteArray(String name, byte[] b) {
        this.tag.setByteArray(name, b);
        return this;
    }

    public NBTBuilder setIntArray(String name, int[] i) {
        this.tag.setIntArray(name, i);
        return this;
    }

    public NBTBuilder setBoolean(String name, boolean b) {
        this.tag.setBoolean(name, b);
        return this;
    }

    /////////////////////////////

    public BlockPos getBlockPos() {
        return getBlockPos("position");
    }

    public BlockPos getBlockPos(String name) {
        return BlockPos.fromLong(this.tag.getLong(name));
    }

    @SuppressWarnings("unchecked")
    public <N extends NBTBase> void getDeserialized(String name, INBTSerializable<N> serializable) {
        serializable.deserializeNBT((N) getTag(name));
    }

    public NBTBase getTag(String name) {
        return this.tag.getTag(name);
    }

    public NBTTagCompound getCompound(String name) {
        return this.tag.getCompoundTag(name);
    }

    public int getInteger(String name) {
        return this.tag.getInteger(name);
    }

    public byte getByte(String name) {
        return this.tag.getByte(name);
    }

    public short getShort(String name) {
        return this.tag.getShort(name);
    }

    public long getLong(String name) {
        return this.tag.getLong(name);
    }

    public float getFloat(String name) {
        return this.tag.getFloat(name);
    }

    public double getDouble(String name) {
        return this.tag.getDouble(name);
    }

    public String getString(String name) {
        return this.tag.getString(name);
    }

    public byte[] getByteArray(String name) {
        return this.tag.getByteArray(name);
    }

    public int[] getIntArray(String name) {
        return this.tag.getIntArray(name);
    }

    public NBTTagList getList(String name, int type) {
        return this.tag.getTagList(name, type);
    }

    public boolean getBoolean(String name) {
        return this.tag.getBoolean(name);
    }

    /////////////////////////////

    public byte getTagId(String name) {
        return this.tag.getTagId(name);
    }

    public boolean contains(String name) {
        return this.tag.hasKey(name);
    }

    public boolean contains(String name, int type) {
        return this.tag.hasKey(name, type);
    }

    public void remove(String name) {
        this.tag.removeTag(name);
    }

    public boolean isEmpty() {
        return this.tag.hasNoTags();
    }

    public NBTBuilder copy() {
        return NBTBuilder.from(this.tag.copy());
    }

    public NBTBuilder mergeWith(NBTBuilder other) {
        this.tag.merge(other.tag);
        return this;
    }

    /////////////////////////////

    @Override
    public NBTTagCompound serializeNBT() {
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (this.tag.hasNoTags()) {
            this.tag = nbt;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public NBTTagCompound get() {
        return serializeNBT();
    }

}
