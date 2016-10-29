package elec332.core.network.impl;

import elec332.core.api.network.ElecByteBuf;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufProcessor;
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

    public BlockPos readBlockPos()
    {
        return BlockPos.fromLong(this.readLong());
    }

    public ElecByteBuf writeBlockPos(BlockPos pos) {
        this.writeLong(pos.toLong());
        return this;
    }

    public ITextComponent readTextComponent() throws IOException {
        return ITextComponent.Serializer.jsonToComponent(this.readString());
    }

    public ElecByteBuf writeTextComponent(ITextComponent component) {
        return this.writeString(ITextComponent.Serializer.componentToJson(component));
    }

    public <T extends Enum<T>> T readEnumValue(Class<T> enumClass) {
        return enumClass.getEnumConstants()[this.readVarInt()];
    }

    public ElecByteBuf writeEnumValue(Enum<?> value) {
        return this.writeVarInt(value.ordinal());
    }


    //Link-through

    public int capacity() {
        return this.buf.capacity();
    }

    public ElecByteBuf capacity(int p_capacity_1_) {
        this.buf.capacity(p_capacity_1_);
        return this;
    }

    public int maxCapacity() {
        return this.buf.maxCapacity();
    }

    public ByteBufAllocator alloc() {
        return this.buf.alloc();
    }

    public ByteOrder order() {
        return this.buf.order();
    }

    public ElecByteBuf order(ByteOrder p_order_1_) {
        this.buf.order(p_order_1_);return this;

    }

    public ElecByteBuf unwrap() {
        this.buf.unwrap();
        return this;
    }

    public boolean isDirect() {
        return this.buf.isDirect();
    }

    public int readerIndex() {
        return this.buf.readerIndex();
    }

    public ElecByteBuf readerIndex(int p_readerIndex_1_) {
        this.buf.readerIndex(p_readerIndex_1_);
        return this;
    }

    public int writerIndex() {
        return this.buf.writerIndex();
    }

    public ElecByteBuf writerIndex(int p_writerIndex_1_) {
        this.buf.writerIndex(p_writerIndex_1_);
        return this;
    }

    public ElecByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_) {
        this.buf.setIndex(p_setIndex_1_, p_setIndex_2_);
        return this;
    }

    public int readableBytes() {
        return this.buf.readableBytes();
    }

    public int writableBytes() {
        return this.buf.writableBytes();
    }

    public int maxWritableBytes() {
        return this.buf.maxWritableBytes();
    }

    public boolean isReadable() {
        return this.buf.isReadable();
    }

    public boolean isReadable(int p_isReadable_1_) {
        return this.buf.isReadable(p_isReadable_1_);
    }

    public boolean isWritable() {
        return this.buf.isWritable();
    }

    public boolean isWritable(int p_isWritable_1_) {
        return this.buf.isWritable(p_isWritable_1_);
    }

    public ElecByteBuf clear() {
        this.buf.clear();
        return this;
    }

    public ElecByteBuf markReaderIndex() {
        this.buf.markReaderIndex();
        return this;
    }

    public ElecByteBuf resetReaderIndex() {
        this.buf.resetReaderIndex();
        return this;
    }

    public ElecByteBuf markWriterIndex() {
        this.buf.markWriterIndex();
        return this;
    }

    public ElecByteBuf resetWriterIndex() {
        this.buf.resetWriterIndex();
        return this;
    }

    public ElecByteBuf discardReadBytes() {
        this.buf.discardReadBytes();
        return this;
    }

    public ElecByteBuf discardSomeReadBytes() {
        this.buf.discardSomeReadBytes();
        return this;
    }

    public ElecByteBuf ensureWritable(int p_ensureWritable_1_) {
        this.buf.ensureWritable(p_ensureWritable_1_);
        return this;
    }

    public int ensureWritable(int p_ensureWritable_1_, boolean p_ensureWritable_2_) {
        return this.buf.ensureWritable(p_ensureWritable_1_, p_ensureWritable_2_);
    }

    public boolean getBoolean(int p_getBoolean_1_) {
        return this.buf.getBoolean(p_getBoolean_1_);
    }

    public byte getByte(int p_getByte_1_) {
        return this.buf.getByte(p_getByte_1_);
    }

    public short getUnsignedByte(int p_getUnsignedByte_1_) {
        return this.buf.getUnsignedByte(p_getUnsignedByte_1_);
    }

    public short getShort(int p_getShort_1_) {
        return this.buf.getShort(p_getShort_1_);
    }

    public int getUnsignedShort(int p_getUnsignedShort_1_) {
        return this.buf.getUnsignedShort(p_getUnsignedShort_1_);
    }

    public int getMedium(int p_getMedium_1_) {
        return this.buf.getMedium(p_getMedium_1_);
    }

    public int getUnsignedMedium(int p_getUnsignedMedium_1_) {
        return this.buf.getUnsignedMedium(p_getUnsignedMedium_1_);
    }

    public int getInt(int p_getInt_1_) {
        return this.buf.getInt(p_getInt_1_);
    }

    public long getUnsignedInt(int p_getUnsignedInt_1_) {
        return this.buf.getUnsignedInt(p_getUnsignedInt_1_);
    }

    public long getLong(int p_getLong_1_) {
        return this.buf.getLong(p_getLong_1_);
    }

    public char getChar(int p_getChar_1_) {
        return this.buf.getChar(p_getChar_1_);
    }

    public float getFloat(int p_getFloat_1_) {
        return this.buf.getFloat(p_getFloat_1_);
    }

    public double getDouble(int p_getDouble_1_) {
        return this.buf.getDouble(p_getDouble_1_);
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_, p_getBytes_4_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_) {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_);
        return this;
    }

    public ElecByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException {
        this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
        return this;
    }

    public int getBytes(int p_getBytes_1_, GatheringByteChannel p_getBytes_2_, int p_getBytes_3_) throws IOException {
        return this.buf.getBytes(p_getBytes_1_, p_getBytes_2_, p_getBytes_3_);
    }

    public ElecByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_) {
        this.buf.setBoolean(p_setBoolean_1_, p_setBoolean_2_);
        return this;
    }

    public ElecByteBuf setByte(int p_setByte_1_, int p_setByte_2_) {
        this.buf.setByte(p_setByte_1_, p_setByte_2_);
        return this;
    }

    public ElecByteBuf setShort(int p_setShort_1_, int p_setShort_2_) {
        this.buf.setShort(p_setShort_1_, p_setShort_2_);
        return this;
    }

    public ElecByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_) {
        this.buf.setMedium(p_setMedium_1_, p_setMedium_2_);
        return this;
    }

    public ElecByteBuf setInt(int p_setInt_1_, int p_setInt_2_) {
        this.buf.setInt(p_setInt_1_, p_setInt_2_);
        return this;
    }

    public ElecByteBuf setLong(int p_setLong_1_, long p_setLong_2_) {
        this.buf.setLong(p_setLong_1_, p_setLong_2_);
        return this;
    }

    public ElecByteBuf setChar(int p_setChar_1_, int p_setChar_2_) {
        this.buf.setChar(p_setChar_1_, p_setChar_2_);
        return this;
    }

    public ElecByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_) {
        this.buf.setFloat(p_setFloat_1_, p_setFloat_2_);
        return this;
    }

    public ElecByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_) {
        this.buf.setDouble(p_setDouble_1_, p_setDouble_2_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_, p_setBytes_4_);
        return this;
    }

    public ElecByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_) {
        this.buf.setBytes(p_setBytes_1_, p_setBytes_2_);
        return this;
    }

    public int setBytes(int p_setBytes_1_, InputStream p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    public int setBytes(int p_setBytes_1_, ScatteringByteChannel p_setBytes_2_, int p_setBytes_3_) throws IOException {
        return this.buf.setBytes(p_setBytes_1_, p_setBytes_2_, p_setBytes_3_);
    }

    public ElecByteBuf setZero(int p_setZero_1_, int p_setZero_2_) {
        this.buf.setZero(p_setZero_1_, p_setZero_2_);
        return this;
    }

    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    public byte readByte() {
        return this.buf.readByte();
    }

    public short readUnsignedByte() {
        return this.buf.readUnsignedByte();
    }

    public short readShort() {
        return this.buf.readShort();
    }

    public int readUnsignedShort() {
        return this.buf.readUnsignedShort();
    }

    public int readMedium() {
        return this.buf.readMedium();
    }

    public int readUnsignedMedium() {
        return this.buf.readUnsignedMedium();
    }

    public int readInt() {
        return this.buf.readInt();
    }

    public long readUnsignedInt() {
        return this.buf.readUnsignedInt();
    }

    public long readLong() {
        return this.buf.readLong();
    }

    public char readChar() {
        return this.buf.readChar();
    }

    public float readFloat() {
        return this.buf.readFloat();
    }

    public double readDouble() {
        return this.buf.readDouble();
    }

    public ElecByteBuf readBytes(int p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    public ElecByteBuf readSlice(int p_readSlice_1_) {
        this.buf.readSlice(p_readSlice_1_);
        return this;
    }

    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
        return this;
    }

    public ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
        return this;
    }

    public ElecByteBuf readBytes(byte[] p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    public ElecByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_) {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_, p_readBytes_3_);
        return this;
    }

    public ElecByteBuf readBytes(ByteBuffer p_readBytes_1_) {
        this.buf.readBytes(p_readBytes_1_);
        return this;
    }

    public ElecByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException {
        this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
        return this;
    }

    public int readBytes(GatheringByteChannel p_readBytes_1_, int p_readBytes_2_) throws IOException {
        return this.buf.readBytes(p_readBytes_1_, p_readBytes_2_);
    }

    public ElecByteBuf skipBytes(int p_skipBytes_1_) {
        this.buf.skipBytes(p_skipBytes_1_);
        return this;
    }

    public ElecByteBuf writeBoolean(boolean p_writeBoolean_1_) {
        this.buf.writeBoolean(p_writeBoolean_1_);
        return this;
    }

    public ElecByteBuf writeByte(int p_writeByte_1_) {
        this.buf.writeByte(p_writeByte_1_);
        return this;
    }

    public ElecByteBuf writeShort(int p_writeShort_1_) {
        this.buf.writeShort(p_writeShort_1_);
        return this;
    }

    public ElecByteBuf writeMedium(int p_writeMedium_1_) {
        this.buf.writeMedium(p_writeMedium_1_);
        return this;
    }

    public ElecByteBuf writeInt(int p_writeInt_1_) {
        this.buf.writeInt(p_writeInt_1_);
        return this;
    }

    public ElecByteBuf writeLong(long p_writeLong_1_) {
        this.buf.writeLong(p_writeLong_1_);
        return this;
    }

    public ElecByteBuf writeChar(int p_writeChar_1_) {
        this.buf.writeChar(p_writeChar_1_);
        return this;
    }

    public ElecByteBuf writeFloat(float p_writeFloat_1_) {
        this.buf.writeFloat(p_writeFloat_1_);
        return this;
    }

    public ElecByteBuf writeDouble(double p_writeDouble_1_) {
        this.buf.writeDouble(p_writeDouble_1_);
        return this;
    }

    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
        return this;
    }

    public ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
        return this;
    }

    public ElecByteBuf writeBytes(byte[] p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    public ElecByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_) {
        this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_, p_writeBytes_3_);
        return this;
    }

    public ElecByteBuf writeBytes(ByteBuffer p_writeBytes_1_) {
        this.buf.writeBytes(p_writeBytes_1_);
        return this;
    }

    public int writeBytes(InputStream p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    public int writeBytes(ScatteringByteChannel p_writeBytes_1_, int p_writeBytes_2_) throws IOException {
        return this.buf.writeBytes(p_writeBytes_1_, p_writeBytes_2_);
    }

    public ElecByteBuf writeZero(int p_writeZero_1_) {
        this.buf.writeZero(p_writeZero_1_);
        return this;
    }

    public int indexOf(int p_indexOf_1_, int p_indexOf_2_, byte p_indexOf_3_) {
        return this.buf.indexOf(p_indexOf_1_, p_indexOf_2_, p_indexOf_3_);
    }

    public int bytesBefore(byte p_bytesBefore_1_) {
        return this.buf.bytesBefore(p_bytesBefore_1_);
    }

    public int bytesBefore(int p_bytesBefore_1_, byte p_bytesBefore_2_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_);
    }

    public int bytesBefore(int p_bytesBefore_1_, int p_bytesBefore_2_, byte p_bytesBefore_3_) {
        return this.buf.bytesBefore(p_bytesBefore_1_, p_bytesBefore_2_, p_bytesBefore_3_);
    }

    public int forEachByte(ByteBufProcessor p_forEachByte_1_) {
        return this.buf.forEachByte(p_forEachByte_1_);
    }

    public int forEachByte(int p_forEachByte_1_, int p_forEachByte_2_, ByteBufProcessor p_forEachByte_3_) {
        return this.buf.forEachByte(p_forEachByte_1_, p_forEachByte_2_, p_forEachByte_3_);
    }

    public int forEachByteDesc(ByteBufProcessor p_forEachByteDesc_1_) {
        return this.buf.forEachByteDesc(p_forEachByteDesc_1_);
    }

    public int forEachByteDesc(int p_forEachByteDesc_1_, int p_forEachByteDesc_2_, ByteBufProcessor p_forEachByteDesc_3_) {
        return this.buf.forEachByteDesc(p_forEachByteDesc_1_, p_forEachByteDesc_2_, p_forEachByteDesc_3_);
    }

    public ElecByteBuf copy() {
        this.buf.copy();
        return this;
    }

    public ElecByteBuf copy(int p_copy_1_, int p_copy_2_) {
        this.buf.copy(p_copy_1_, p_copy_2_);
        return this;
    }

    public ElecByteBuf slice() {
        this.buf.slice();
        return this;
    }

    public ElecByteBuf slice(int p_slice_1_, int p_slice_2_) {
        this.buf.slice(p_slice_1_, p_slice_2_);
        return this;
    }

    public ElecByteBuf duplicate() {
        this.buf.duplicate();
        return this;
    }

    public int nioBufferCount() {
        return this.buf.nioBufferCount();
    }

    public ByteBuffer nioBuffer() {
        return this.buf.nioBuffer();
    }

    public ByteBuffer nioBuffer(int p_nioBuffer_1_, int p_nioBuffer_2_) {
        return this.buf.nioBuffer(p_nioBuffer_1_, p_nioBuffer_2_);
    }

    public ByteBuffer internalNioBuffer(int p_internalNioBuffer_1_, int p_internalNioBuffer_2_) {
        return this.buf.internalNioBuffer(p_internalNioBuffer_1_, p_internalNioBuffer_2_);
    }

    public ByteBuffer[] nioBuffers() {
        return this.buf.nioBuffers();
    }

    public ByteBuffer[] nioBuffers(int p_nioBuffers_1_, int p_nioBuffers_2_) {
        return this.buf.nioBuffers(p_nioBuffers_1_, p_nioBuffers_2_);
    }

    public boolean hasArray() {
        return this.buf.hasArray();
    }

    public byte[] array() {
        return this.buf.array();
    }

    public int arrayOffset() {
        return this.buf.arrayOffset();
    }

    public boolean hasMemoryAddress() {
        return this.buf.hasMemoryAddress();
    }

    public long memoryAddress() {
        return this.buf.memoryAddress();
    }

    public String toString(Charset p_toString_1_) {
        return this.buf.toString(p_toString_1_);
    }

    public String toString(int p_toString_1_, int p_toString_2_, Charset p_toString_3_) {
        return this.buf.toString(p_toString_1_, p_toString_2_, p_toString_3_);
    }

    public int hashCode() {
        return this.buf.hashCode();
    }

    @SuppressWarnings("all")
    public boolean equals(Object p_equals_1_) {
        return this.buf.equals(p_equals_1_);
    }

    public int compareTo(ByteBuf p_compareTo_1_) {
        return this.buf.compareTo(p_compareTo_1_);
    }

    public String toString() {
        return this.buf.toString();
    }

    public ElecByteBuf retain(int p_retain_1_) {
        this.buf.retain(p_retain_1_);
        return this;
    }

    public ElecByteBuf retain() {
        this.buf.retain();
        return this;
    }

    public int refCnt() {
        return this.buf.refCnt();
    }

    public boolean release() {
        return this.buf.release();
    }

    public boolean release(int p_release_1_) {
        return this.buf.release(p_release_1_);
    }

}
