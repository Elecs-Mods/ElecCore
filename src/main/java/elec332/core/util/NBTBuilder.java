package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 27-7-2018
 * <p>
 * Builder for {@link CompoundNBT}
 * Can be used to chain writes to a {@link CompoundNBT}
 */
public class NBTBuilder implements INBTSerializable<CompoundNBT>, Supplier<CompoundNBT> {

    public static NBTBuilder from(CompoundNBT tag) {
        return new NBTBuilder(tag);
    }

    public NBTBuilder() {
        this(new CompoundNBT());
    }

    public NBTBuilder(CompoundNBT tag) {
        this.tag = Preconditions.checkNotNull(tag);
    }

    private CompoundNBT tag;

    /////////////////////////////

    public NBTBuilder setBlockPos(BlockPos pos) {
        return setBlockPos("position", pos);
    }

    public NBTBuilder setBlockPos(String name, BlockPos pos) {
        return setLong(name, pos.toLong());
    }

    public NBTBuilder setUUID(String name, UUID uuid) {
        return setString(name, uuid.toString());
    }

    public NBTBuilder setVec(String name, Vec3d vec) {
        setDouble(name + "_vecx", vec.x);
        setDouble(name + "_vecy", vec.y);
        setDouble(name + "_vecz", vec.z);
        return this;
    }

    public NBTBuilder setColor(String name, @Nullable DyeColor color) {
        if (color != null) {
            setInteger(name, color.getId());
        }
        return this;
    }

    public NBTBuilder setResourceLocation(String name, @Nonnull ResourceLocation rl) {
        return setString(name, rl.toString());
    }

    public <T extends IForgeRegistryEntry<T>> NBTBuilder setRegistryObject(String name, T obj) {
        return setResourceLocation(name, Preconditions.checkNotNull(obj.getRegistryName()));
    }

    public <T extends Enum<T>> NBTBuilder setEnum(String name, T type) {
        return setShort(name, (short) type.ordinal());
    }

    public NBTBuilder setTag(String name, INBTSerializable<?> tag) {
        return setTag(name, tag.serializeNBT());
    }

    public NBTBuilder setTag(String name, INBT tag) {
        this.tag.put(name, tag);
        return this;
    }

    public NBTBuilder setInteger(String name, int i) {
        this.tag.putInt(name, i);
        return this;
    }

    public NBTBuilder setByte(String name, byte b) {
        this.tag.putByte(name, b);
        return this;
    }

    public NBTBuilder setShort(String name, short s) {
        this.tag.putShort(name, s);
        return this;
    }

    public NBTBuilder setLong(String name, long l) {
        this.tag.putLong(name, l);
        return this;
    }

    public NBTBuilder setFloat(String name, float f) {
        this.tag.putFloat(name, f);
        return this;
    }

    public NBTBuilder setDouble(String name, double d) {
        this.tag.putDouble(name, d);
        return this;
    }

    public NBTBuilder setString(String name, String str) {
        this.tag.putString(name, str);
        return this;
    }

    public NBTBuilder setByteArray(String name, byte[] b) {
        this.tag.putByteArray(name, b);
        return this;
    }

    public NBTBuilder setIntArray(String name, int[] i) {
        this.tag.putIntArray(name, i);
        return this;
    }

    public NBTBuilder setBoolean(String name, boolean b) {
        this.tag.putBoolean(name, b);
        return this;
    }

    /////////////////////////////

    public BlockPos getBlockPos() {
        return getBlockPos("position");
    }

    public BlockPos getBlockPos(String name) {
        return BlockPos.fromLong(getLong(name));
    }

    public UUID getUUID(String name) {
        return UUID.fromString(getString(name));
    }

    public Vec3d getVec(String name) {
        return new Vec3d(getDouble(name + "_vecx"), getDouble(name + "_vecy"), getDouble(name + "_vecz"));
    }

    public DyeColor getColor(String name) {
        if (!contains(name)) {
            return null;
        }
        return DyeColor.byId(getInteger(name));
    }

    public ResourceLocation getResourceLocation(String name) {
        return new ResourceLocation(getString(name));
    }

    public <T extends IForgeRegistryEntry<T>> T getRegistryObject(String name, IForgeRegistry<T> registry) {
        return registry.getValue(getResourceLocation(name));
    }

    public <T extends Enum<T>> T getEnum(String name, Class<T> type) {
        return type.getEnumConstants()[getInteger(name)];
    }

    @SuppressWarnings("unchecked")
    public <N extends INBT> void getDeserialized(String name, INBTSerializable<N> serializable) {
        serializable.deserializeNBT((N) getTag(name));
    }

    public INBT getTag(String name) {
        return this.tag.get(name);
    }

    public CompoundNBT getCompound(String name) {
        return this.tag.getCompound(name);
    }

    public int getInteger(String name) {
        return this.tag.getInt(name);
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

    public ListNBT getTagList(String name, int type) {
        return this.tag.getList(name, type);
    }

    public boolean getBoolean(String name) {
        return this.tag.getBoolean(name);
    }

    /////////////////////////////

    public byte getTagId(String name) {
        return this.tag.getTagId(name);
    }

    public boolean contains(String name) {
        return this.tag.contains(name);
    }

    public boolean contains(String name, int type) {
        return this.tag.contains(name, type);
    }

    public void remove(String name) {
        this.tag.remove(name);
    }

    public boolean isEmpty() {
        return this.tag.isEmpty();
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
    public CompoundNBT serializeNBT() {
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (this.tag.isEmpty()) {
            this.tag = nbt;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public CompoundNBT get() {
        return serializeNBT();
    }

}
