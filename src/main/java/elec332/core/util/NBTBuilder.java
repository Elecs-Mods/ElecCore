package elec332.core.util;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.NBTTagCompound;
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
        this.tag.setLong("position", pos.toLong());
        return this;
    }

    public NBTBuilder setTag(String name, NBTTagCompound tag) {
        this.tag.setTag(name, tag);
        return this;
    }

    public NBTBuilder setInteger(String name, int i) {
        this.tag.setInteger(name, i);
        return this;
    }

    /////////////////////////////

    public BlockPos getBlockPos() {
        return BlockPos.fromLong(this.tag.getLong("position"));
    }

    public NBTTagCompound getTag(String name) {
        return this.tag.getCompoundTag(name);
    }

    public int getInteger(String name) {
        return this.tag.getInteger(name);
    }

    /////////////////////////////

    @Override
    public NBTTagCompound serializeNBT() {
        return tag;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (tag.hasNoTags()) {
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
