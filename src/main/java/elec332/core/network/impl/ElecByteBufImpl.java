package elec332.core.network.impl;

import elec332.core.api.network.ElecByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.util.ByteProcessor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * Created by Elec332 on 25-10-2016.
 */
class ElecByteBufImpl extends ElecByteBuf {

    ElecByteBufImpl(ByteBuf buffer) {
        this.buf = buffer instanceof ElecByteBufImpl ? ((ElecByteBufImpl) buffer).buf : buffer;
    }

    private ByteBuf buf;

    @Override
    public ElecByteBuf writeNBTTagCompoundToBuffer(@Nullable NBTTagCompound tag) {
        ByteBufUtils.writeTag(this, tag);
        return this;
    }

    @Nullable
    @Override
    public NBTTagCompound readNBTTagCompoundFromBuffer() {
        return ByteBufUtils.readTag(this);
    }

    @Override
    public ElecByteBuf writeItemStackToBuffer(@Nullable ItemStack stack) {
        ByteBufUtils.writeItemStack(this, stack);
        return this;
    }

    @Nullable
    @Override
    public ItemStack readItemStackFromBuffer() {
        return ByteBufUtils.readItemStack(this);
    }

    @Override
    public ElecByteBuf writeString(String string) {
        ByteBufUtils.writeUTF8String(this, string);
        return this;
    }

    @Override
    public String readString() {
        return ByteBufUtils.readUTF8String(this);
    }

    @Override
    public ElecByteBuf writeVarInt(int toWrite, int maxSize) {
        ByteBufUtils.writeVarInt(this, toWrite, maxSize);
        return this;
    }

    @Override
    public int readVarInt(int maxSize) {
        return ByteBufUtils.readVarInt(this, maxSize);
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
    public BlockPos readBlockPos()
    {
        return BlockPos.fromLong(this.readLong());
    }

    @Override
    public ElecByteBuf writeBlockPos(BlockPos pos) {
        this.writeLong(pos.toLong());
        return this;
    }

    @Override
    public ITextComponent readTextComponent() throws IOException {
        return ITextComponent.Serializer.jsonToComponent(this.readString());
    }

    @Override
    public ElecByteBuf writeTextComponent(ITextComponent component) {
        return this.writeString(ITextComponent.Serializer.componentToJson(component));
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
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public ElecByteBuf capacity(int p_capacity_1_) {
        this.buf.capacity(p_capacity_1_);
        return this;
    }

    @Override
    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    @Override
    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ByteOrder order() {
        return this.buf.order();
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public ElecByteBuf order(ByteOrder p_order_1_) {
        this.buf.order(p_order_1_);return this;
    }

    @Override
    public ElecByteBuf unwrap() {
        this.buf.unwrap();
        return this;
    }

    @Override
    public boolean isDirect() {
        return this.buf.isDirect();
    }

    @Override
    public boolean isReadOnly() {
        return this.buf.isReadOnly();
    }

    @Override
    public ElecByteBuf asReadOnly() {
        this.buf.asReadOnly();
        return this;
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public ElecByteBuf readerIndex(int p_readerIndex_1_) {
        this.buf.readerIndex(p_readerIndex_1_);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
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
    public int readableBytes() {
        return this.buf.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buf.writableBytes();
    }

    @Override
    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    @Override
    public boolean isReadable() {
        return this.buf.isReadable();
    }

    @Override
    public boolean isReadable(int p_isReadable_1_) {
        return this.buf.isReadable(p_isReadable_1_);
    }

    @Override
    public boolean isWritable() {
        return this.buf.isWritable();
    }

    @Override
    public boolean isWritable(int p_isWritable_1_) {
        return this.buf.isWritable(p_isWritable_1_);
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
    public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
        return this.buf.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
    }

    @Override
    public boolean getBoolean(int p_getBoolean_1_) {
        return this.buf.getBoolean(p_getBoolean_1_);
    }

    @Override
    public byte getByte(int p_getByte_1_) {
        return this.buf.getByte(p_getByte_1_);
    }

    @Override
    public short getUnsignedByte(int p_getUnsignedByte_1_) {
        return this.buf.getUnsignedByte(p_getUnsignedByte_1_);
    }

    @Override
    public short getShort(int p_getShort_1_) {
        return this.buf.getShort(p_getShort_1_);
    }

    @Override
    public short getShortLE(int index) {
        return this.buf.getShortLE(index);
    }

    @Override
    public int getUnsignedShort(int p_getUnsignedShort_1_) {
        return this.buf.getUnsignedShort(p_getUnsignedShort_1_);
    }

    @Override
    public int getUnsignedShortLE(int index) {
        return this.buf.getUnsignedShortLE(index);
    }

    @Override
    public int getMedium(int p_getMedium_1_) {
        return this.buf.getMedium(p_getMedium_1_);
    }

    @Override
    public int getMediumLE(int index) {
        return this.buf.getMediumLE(index);
    }

    @Override
    public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
        return this.buf.getUnsignedMedium(p_getUnsignedMedium_1_);
    }

    @Override
    public int getUnsignedMediumLE(int index) {
        return this.buf.getUnsignedMediumLE(index);
    }

    @Override
    public int getInt(int p_getInt_1_) {
        return this.buf.getInt(p_getInt_1_);
    }

    @Override
    public int getIntLE(int index) {
        return this.buf.getIntLE(index);
    }

    @Override
    public long getUnsignedInt(int p_getUnsignedInt_1_) {
        return this.buf.getUnsignedInt(p_getUnsignedInt_1_);
    }

    @Override
    public long getUnsignedIntLE(int index) {
        return this.buf.getUnsignedIntLE(index);
    }

    @Override
    public long getLong(int p_getLong_1_) {
        return this.buf.getLong(p_getLong_1_);
    }

    @Override
    public long getLongLE(int index) {
        return this.buf.getLongLE(index);
    }

    @Override
    public char getChar(int p_getChar_1_) {
        return this.buf.getChar(p_getChar_1_);
    }

    @Override
    public float getFloat(int p_getFloat_1_) {
        return this.buf.getFloat(p_getFloat_1_);
    }

    @Override
    public double getDouble(int p_getDouble_1_) {
        return this.buf.getDouble(p_getDouble_1_);
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
    public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    @Override
    public int getBytes(int index, FileChannel out, long position, int length) throws IOException {
        return this.buf.getBytes(index, out, position, length);
    }

    @Override
    public CharSequence getCharSequence(int index, int length, Charset charset) {
        return this.buf.getCharSequence(index, length, charset);
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
    public ByteBuf setShortLE(int index, int value) {
        return this.buf.setShortLE(index, value);
    }

    @Override
    public ElecByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        this.buf.setMedium(p_setMedium_1_, p_setMedium_2_);
        return this;
    }

    @Override
    public ByteBuf setMediumLE(int index, int value) {
        return this.buf.setMediumLE(index, value);
    }

    @Override
    public ElecByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        this.buf.setInt(p_setInt_1_, p_setInt_2_);
        return this;
    }

    @Override
    public ByteBuf setIntLE(int index, int value) {
        return this.buf.setIntLE(index, value);
    }

    @Override
    public ElecByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        this.buf.setLong(p_setLong_1_, p_setLong_2_);
        return this;
    }

    @Override
    public ByteBuf setLongLE(int index, long value) {
        return this.buf.setLongLE(index, value);
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
    public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    @Override
    public int setBytes(int index, FileChannel in, long position, int length) throws IOException {
        return this.buf.setBytes(index, in, position, length);
    }

    @Override
    public ElecByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        this.buf.setZero(p_setZero_1_, p_setZero_2_);
        return this;
    }

    @Override
    public int setCharSequence(int index, CharSequence sequence, Charset charset) {
        return this.buf.setCharSequence(index, sequence, charset);
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.buf.readShortLE();
    }

    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    @Override
    public int readUnsignedShortLE() {
        return this.buf.readUnsignedShortLE();
    }

    @Override
    public int readMedium() {
        return this.buf.readMedium();
    }

    @Override
    public int readMediumLE() {
        return this.buf.readMediumLE();
    }

    @Override
    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    @Override
    public int readUnsignedMediumLE() {
        return this.buf.readUnsignedMediumLE();
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buf.readIntLE();
    }

    @Override
    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    @Override
    public long readUnsignedIntLE() {
        return this.buf.readUnsignedIntLE();
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buf.readLongLE();
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public ElecByteBuf readBytes(int p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    @Override
    public ElecByteBuf readSlice(int p_readSlice_1_) {
        this.buf.readSlice(p_readSlice_1_);
        return this;
    }

    @Override
    public ElecByteBuf readRetainedSlice(int length) {
        this.buf.readRetainedSlice(length);
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
    public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    @Override
    public CharSequence readCharSequence(int length, Charset charset) {
        return this.buf.readCharSequence(length, charset);
    }

    @Override
    public int readBytes(FileChannel out, long position, int length) throws IOException {
        return this.buf.readBytes(out, position, length);
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
    public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    @Override
    public int writeBytes(FileChannel in, long position, int length) throws IOException {
        return this.buf.writeBytes(in, position, length);
    }

    @Override
    public ElecByteBuf writeZero(int p_writeZero_1_) {
        this.buf.writeZero(p_writeZero_1_);
        return this;
    }

    @Override
    public int writeCharSequence(CharSequence sequence, Charset charset) {
        return this.buf.writeCharSequence(sequence, charset);
    }

    @Override
    public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
        return this.buf.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
    }

    @Override
    public int bytesBefore(byte p_bytesBefore_1_) {
        return this.buf.bytesBefore(p_bytesBefore_1_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
    }

    @Override
    public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
    }

    @Override
    public int forEachByte(ByteProcessor processor) {
        return this.buf.forEachByte(processor);
    }

    @Override
    public int forEachByte(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByte(index, length, processor);
    }

    @Override
    public int forEachByteDesc(ByteProcessor processor) {
        return this.buf.forEachByteDesc(processor);
    }

    @Override
    public int forEachByteDesc(int index, int length, ByteProcessor processor) {
        return this.buf.forEachByteDesc(index, length, processor);
    }

    @Override
    public ElecByteBuf copy() {
        this.buf.copy();
        return this;
    }

    @Override
    public ElecByteBuf copy(int p_copy_1_, int p_copy_2_) {
        this.buf.copy(p_copy_1_, p_copy_2_);
        return this;
    }

    @Override
    public ElecByteBuf slice() {
        this.buf.slice();
        return this;
    }

    @Override
    public ElecByteBuf retainedSlice() {
        this.buf.retainedSlice();
        return this;
    }

    @Override
    public ElecByteBuf slice(int p_slice_1_, int p_slice_2_) {
        this.buf.slice(p_slice_1_, p_slice_2_);
        return this;
    }

    @Override
    public ElecByteBuf retainedSlice(int index, int length) {
        this.buf.retainedSlice(index, length);
        return this;
    }

    @Override
    public ElecByteBuf duplicate() {
        this.buf.duplicate();
        return this;
    }

    @Override
    public ElecByteBuf retainedDuplicate() {
        this.buf.retainedDuplicate();
        return this;
    }

    @Override
    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    @Override
    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    @Override
    public ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
        return this.buf.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
    }

    @Override
    public ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
        return this.buf.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
    }

    @Override
    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    @Override
    public ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
        return this.buf.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    @Override
    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    @Override
    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    @Override
    public String toString(Charset p_toString_1_) {
        return this.buf.toString(p_toString_1_);
    }

    @Override
    public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
        return this.buf.toString(p_toString_1_, p_toString_2_, p_toString_3_);
    }

    @Override
    public int hashCode() {
        return this.buf.hashCode();
    }

    @Override
    @SuppressWarnings("all")
    public boolean equals(Object p_equals_1_) {
        return this.buf.equals(p_equals_1_);
    }

    @Override
    public int compareTo(ByteBuf p_compareTo_1_) {
        return this.buf.compareTo(p_compareTo_1_);
    }

    @Override
    public String toString() {
        return this.buf.toString();
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

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int p_release_1_) {
        return this.buf.release(p_release_1_);
    }

}
