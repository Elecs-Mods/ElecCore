package elec332.core.network.impl;

import com.google.common.base.MoreObjects;
import elec332.core.api.network.ElecByteBuf;
import elec332.core.util.ItemStackHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.Validate;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by Elec332 on 25-10-2016.
 */
class ElecByteBufImpl extends ElecByteBuf {

    ElecByteBufImpl(ByteBuf buffer) {
        super(buffer instanceof ElecByteBufImpl ? ((ElecByteBufImpl) buffer).buf : buffer);
        buf = buffer instanceof ElecByteBufImpl ? ((ElecByteBufImpl) buffer).buf : buffer;
    }

    private ByteBuf buf;

    @Override
    public ElecByteBuf writeCompoundNBTToBuffer(@Nullable CompoundNBT tag) {
        writeCompoundTag(tag);
        return this;
    }

    @Nullable
    @Override
    public CompoundNBT readCompoundNBTFromBuffer() {
        return readCompoundTag();
    }

    @Override
    public ElecByteBuf writeItemStackToBuffer(@Nullable ItemStack stack) {
        writeItemStack(MoreObjects.firstNonNull(stack, ItemStackHelper.NULL_STACK));
        return this;
    }

    @Nullable
    @Override
    public ItemStack readItemStackFromBuffer() {
        return readItemStack();
    }

    @Override
    public ElecByteBuf writeString(String string) {
        writeString(string, Short.MAX_VALUE);
        return this;
    }

    @Override
    public String readString() {
        return readString(Short.MAX_VALUE);
    }

    @Override
    public ElecByteBuf writeVarInt(int toWrite, int maxSize) {
        Validate.isTrue(varIntByteCount(toWrite) <= maxSize, "Integer is too big for %d bytes", maxSize);
        while ((toWrite & -128) != 0) {
            writeByte(toWrite & 127 | 128);
            toWrite >>>= 7;
        }
        return writeByte(toWrite);
    }

    @Override
    public int readVarInt(int maxSize) {
        Validate.isTrue(maxSize < 6 && maxSize > 0, "Varint length is between 1 and 5, not %d", maxSize);
        int i = 0;
        int j = 0;
        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > maxSize) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b0 & 128) == 128);
        return i;
    }

    @Override
    public ElecByteBuf writeUuid(UUID uuid) {
        this.writeLong(uuid.getMostSignificantBits());
        this.writeLong(uuid.getLeastSignificantBits());
        return this;
    }

    @Override
    public UUID readUuid() {
        return new UUID(this.readLong(), this.readLong());
    }

    @Override
    public BlockPos readBlockPos() {
        return BlockPos.fromLong(this.readLong());
    }

    @Override
    public ElecByteBuf writeBlockPos(BlockPos pos) {
        this.writeLong(pos.toLong());
        return this;
    }

    @Override
    public ITextComponent readTextComponent() {
        return ITextComponent.Serializer.func_240643_a_(this.readString());
    }

    @Override
    public ElecByteBuf writeTextComponent(ITextComponent component) {
        return this.writeString(ITextComponent.Serializer.toJson(component));
    }

    @Override
    public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
        return enumClass.getEnumConstants()[this.readVarInt()];
    }

    @Override
    public ElecByteBuf writeEnumValue(Enum<?> value) {
        return this.writeVarInt(value.ordinal());
    }


    //Link-through

    @Override
    public ElecByteBuf capacity(int p_capacity_1_) {
        this.buf.capacity(p_capacity_1_);
        return this;
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ElecByteBuf order(ByteOrder p_order_1_) {
        this.buf.order(p_order_1_);
        return this;
    }

    @Override
    public ElecByteBuf unwrap() {
        this.buf.unwrap();
        return this;
    }

    @Override
    public ElecByteBuf readerIndex(int p_readerIndex_1_) {
        this.buf.readerIndex(p_readerIndex_1_);
        return this;
    }

    @Override
    public ElecByteBuf writerIndex(int p_writerIndex_1_) {
        this.buf.writerIndex(p_writerIndex_1_);
        return this;
    }

    @Override
    public ElecByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
        this.buf.setIndex(p_setIndex_1_, p_setIndex_2_);
        return this;
    }

    @Override
    public ElecByteBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public ElecByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public ElecByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public ElecByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public ElecByteBuf resetWriterIndex() {
        this.buf.resetWriterIndex();
        return this;
    }

    @Override
    public ElecByteBuf discardReadBytes() {
        this.buf.discardReadBytes();
        return this;
    }

    @Override
    public ElecByteBuf discardSomeReadBytes() {
        this.buf.discardSomeReadBytes();
        return this;
    }

    @Override
    public ElecByteBuf ensureWritable(int p_ensureWritable_1_) {
        this.buf.ensureWritable(p_ensureWritable_1_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
        this.buf.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
        return this;
    }

    @Override
    public ElecByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
        this.buf.setByte(p_setByte_1_, p_setByte_2_);
        return this;
    }

    @Override
    public ElecByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
        this.buf.setShort(p_setShort_1_, p_setShort_2_);
        return this;
    }

    @Override
    public ElecByteBuf setShortLE(int index, int value) {
        this.buf.setShortLE(index, value);
        return this;
    }

    @Override
    public ElecByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        this.buf.setMedium(p_setMedium_1_, p_setMedium_2_);
        return this;
    }

    @Override
    public ElecByteBuf setMediumLE(int index, int value) {
        this.buf.setMediumLE(index, value);
        return this;
    }

    @Override
    public ElecByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        this.buf.setInt(p_setInt_1_, p_setInt_2_);
        return this;
    }

    @Override
    public ElecByteBuf setIntLE(int index, int value) {
        this.buf.setIntLE(index, value);
        return this;
    }

    @Override
    public ElecByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        this.buf.setLong(p_setLong_1_, p_setLong_2_);
        return this;
    }

    @Override
    public ElecByteBuf setLongLE(int index, long value) {
        this.buf.setLongLE(index, value);
        return this;
    }

    @Override
    public ElecByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
        this.buf.setChar(p_setChar_1_, p_setChar_2_);
        return this;
    }

    @Override
    public ElecByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
        this.buf.setFloat(p_setFloat_1_, p_setFloat_2_);
        return this;
    }

    @Override
    public ElecByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
        this.buf.setDouble(p_setDouble_1_, p_setDouble_2_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
        return this;
    }

    @Override
    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        this.buf.setZero(p_setZero_1_, p_setZero_2_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(int p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(byte[] p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(ByteBuffer p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf skipBytes(int p_skipBytes_1_) {
        this.buf.skipBytes(p_skipBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeBoolean(boolean p_writeBoolean_1_) {
        this.buf.writeBoolean(p_writeBoolean_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeByte(int p_writeByte_1_) {
        this.buf.writeByte(p_writeByte_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeShort(int p_writeShort_1_) {
        this.buf.writeShort(p_writeShort_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeShortLE(int value) {
        this.buf.writeShortLE(value);
        return this;
    }

    @Override
    public ElecByteBuf writeMedium(int p_writeMedium_1_) {
        this.buf.writeMedium(p_writeMedium_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeMediumLE(int value) {
        this.buf.writeMediumLE(value);
        return this;
    }

    @Override
    public ElecByteBuf writeInt(int p_writeInt_1_) {
        this.buf.writeInt(p_writeInt_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeIntLE(int value) {
        this.buf.writeIntLE(value);
        return this;
    }

    @Override
    public ElecByteBuf writeLong(long p_writeLong_1_) {
        this.buf.writeLong(p_writeLong_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeLongLE(long value) {
        this.buf.writeLongLE(value);
        return this;
    }

    @Override
    public ElecByteBuf writeChar(int p_writeChar_1_) {
        this.buf.writeChar(p_writeChar_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeFloat(float p_writeFloat_1_) {
        this.buf.writeFloat(p_writeFloat_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeDouble(double p_writeDouble_1_) {
        this.buf.writeDouble(p_writeDouble_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(byte[] p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
        return this;
    }

    @Override
    public ElecByteBuf writeBytes(ByteBuffer p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf writeZero(int p_writeZero_1_) {
        this.buf.writeZero(p_writeZero_1_);
        return this;
    }

    @Override
    public ElecByteBuf copy() {
        return new ElecByteBufImpl(this.buf.copy());
    }

    @Override
    public ElecByteBuf copy(int p_copy_1_, int p_copy_2_) {
        return new ElecByteBufImpl(this.buf.copy(p_copy_1_, p_copy_2_));
    }

    @Override
    public ElecByteBuf duplicate() {
        this.buf.duplicate();
        return this;
    }

    @Override
    public ElecByteBuf retain(int p_retain_1_) {
        this.buf.retain(p_retain_1_);
        return this;
    }

    @Override
    public ElecByteBuf retain() {
        this.buf.retain();
        return this;
    }

    @Override
    public ElecByteBuf touch() {
        this.buf.touch();
        return this;
    }

    @Override
    public ElecByteBuf touch(Object hint) {
        this.buf.touch(hint);
        return this;
    }

    private static int varIntByteCount(int toCount) {
        return (toCount & 0xFFFFFF80) == 0 ? 1 : ((toCount & 0xFFFFC000) == 0 ? 2 : ((toCount & 0xFFE00000) == 0 ? 3 : ((toCount & 0xF0000000) == 0 ? 4 : 5)));
    }

}
