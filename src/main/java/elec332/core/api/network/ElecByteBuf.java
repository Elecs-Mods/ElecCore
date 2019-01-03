package elec332.core.api.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

/**
 * Created by Elec332 on 25-10-2016.
 */
public abstract class ElecByteBuf extends PacketBuffer {

    public ElecByteBuf(ByteBuf buf) {
        super(buf);
    }

    /**
     * Writes a compressed NBTTagCompound to this buffer
     */
    public abstract ElecByteBuf writeNBTTagCompoundToBuffer(@Nullable NBTTagCompound tag);

    /**
     * Reads a compressed NBTTagCompound from this buffer
     */
    @Nullable
    public abstract NBTTagCompound readNBTTagCompoundFromBuffer();

    /**
     * Writes a ItemStack to this buffer
     */
    public abstract ElecByteBuf writeItemStackToBuffer(@Nullable ItemStack stack);

    /**
     * Reads an ItemStack from this buffer
     */
    @Nullable
    public abstract ItemStack readItemStackFromBuffer();

    /**
     * Write a String with UTF8 byte encoding to the buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     *
     * @param string The string to write
     */
    @Override
    public abstract ElecByteBuf writeString(String string);

    /**
     * Read a UTF8 string from the byte buffer.
     * It is encoded as <varint length>[<UTF8 char bytes>]
     *
     * @return The string
     */
    public abstract String readString();

    /**
     * Write an integer to the buffer using variable length encoding. The maxSize constrains
     * how many bytes (and therefore the maximum number) that will be written.
     *
     * @param toWrite The integer to write
     */
    @Override
    public ElecByteBuf writeVarInt(int toWrite) {
        return writeVarInt(toWrite, 5);
    }

    /**
     * Write an integer to the buffer using variable length encoding. The maxSize constrains
     * how many bytes (and therefore the maximum number) that will be written.
     *
     * @param toWrite The integer to write
     * @param maxSize The maximum number of bytes to use
     */
    public abstract ElecByteBuf writeVarInt(int toWrite, int maxSize);

    /**
     * Read a varint from the supplied buffer.
     *
     * @return The integer
     */
    public int readVarInt() {
        return readVarInt(5);
    }

    /**
     * Read a varint from the supplied buffer.
     *
     * @param maxSize The maximum length of bytes to read
     * @return The integer
     */
    public abstract int readVarInt(int maxSize);

    public abstract ElecByteBuf writeUuid(UUID uuid);

    public abstract UUID readUuid();

    @Override
    public abstract ElecByteBuf writeBlockPos(BlockPos pos);

    @Override
    public abstract BlockPos readBlockPos();

    @Override
    public abstract ElecByteBuf writeTextComponent(ITextComponent component);

    @Override
    public abstract ITextComponent readTextComponent();

    @Override
    public abstract ElecByteBuf writeEnumValue(Enum<?> value);

    @Override
    public abstract <T extends Enum<T>> T readEnumValue(Class<T> enumClass);


    // Link-through

    @Override
    public abstract ElecByteBuf capacity(int p_capacity_1_);

    @Override
    @SuppressWarnings("deprecation")
    public abstract ElecByteBuf order(ByteOrder p_order_1_);

    @Override
    public abstract ElecByteBuf unwrap();

    @Override
    public abstract ElecByteBuf readerIndex(int p_readerIndex_1_);

    @Override
    public abstract ElecByteBuf writerIndex(int p_writerIndex_1_);

    @Override
    public abstract ElecByteBuf setIndex(int p_setIndex_1_, int p_setIndex_2_);

    @Override
    public abstract ElecByteBuf clear();

    @Override
    public abstract ElecByteBuf markReaderIndex();

    @Override
    public abstract ElecByteBuf resetReaderIndex();

    @Override
    public abstract ElecByteBuf markWriterIndex();

    @Override
    public abstract ElecByteBuf resetWriterIndex();

    @Override
    public abstract ElecByteBuf discardReadBytes();

    @Override
    public abstract ElecByteBuf discardSomeReadBytes();

    @Override
    public abstract ElecByteBuf ensureWritable(int p_ensureWritable_1_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, ByteBuf p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, byte[] p_getBytes_2_, int p_getBytes_3_, int p_getBytes_4_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, ByteBuffer p_getBytes_2_);

    @Override
    public abstract ElecByteBuf getBytes(int p_getBytes_1_, OutputStream p_getBytes_2_, int p_getBytes_3_) throws IOException;

    @Override
    public abstract ElecByteBuf setBoolean(int p_setBoolean_1_, boolean p_setBoolean_2_);

    @Override
    public abstract ElecByteBuf setByte(int p_setByte_1_, int p_setByte_2_);

    @Override
    public abstract ElecByteBuf setShort(int p_setShort_1_, int p_setShort_2_);

    @Override
    public abstract ElecByteBuf setShortLE(int p_setShortLE_1_, int p_setShortLE_2_);

    @Override
    public abstract ElecByteBuf setMedium(int p_setMedium_1_, int p_setMedium_2_);

    @Override
    public abstract ElecByteBuf setMediumLE(int p_setMediumLE_1_, int p_setMediumLE_2_);

    @Override
    public abstract ElecByteBuf writeMediumLE(int p_writeMediumLE_1_);

    @Override
    public abstract ElecByteBuf setInt(int p_setInt_1_, int p_setInt_2_);

    @Override
    public abstract ElecByteBuf writeIntLE(int p_writeIntLE_1_);

    @Override
    public abstract ElecByteBuf setLong(int p_setLong_1_, long p_setLong_2_);

    @Override
    public abstract ElecByteBuf setLongLE(int p_setLongLE_1_, long p_setLongLE_2_);

    @Override
    public abstract ElecByteBuf setChar(int p_setChar_1_, int p_setChar_2_);

    @Override
    public abstract ElecByteBuf setFloat(int p_setFloat_1_, float p_setFloat_2_);

    @Override
    public abstract ElecByteBuf setDouble(int p_setDouble_1_, double p_setDouble_2_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, ByteBuf p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, byte[] p_setBytes_2_, int p_setBytes_3_, int p_setBytes_4_);

    @Override
    public abstract ElecByteBuf setBytes(int p_setBytes_1_, ByteBuffer p_setBytes_2_);

    @Override
    public abstract ElecByteBuf setZero(int p_setZero_1_, int p_setZero_2_);

    @Override
    public abstract ElecByteBuf readBytes(int p_readBytes_1_);

    @Override
    public abstract ElecByteBuf readBytes(ByteBuf p_readBytes_1_);

    @Override
    public abstract ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_);

    @Override
    public abstract ElecByteBuf readBytes(ByteBuf p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_);

    @Override
    public abstract ElecByteBuf readBytes(byte[] p_readBytes_1_);

    @Override
    public abstract ElecByteBuf readBytes(byte[] p_readBytes_1_, int p_readBytes_2_, int p_readBytes_3_);

    @Override
    public abstract ElecByteBuf readBytes(ByteBuffer p_readBytes_1_);

    @Override
    public abstract ElecByteBuf readBytes(OutputStream p_readBytes_1_, int p_readBytes_2_) throws IOException;

    @Override
    public abstract ElecByteBuf skipBytes(int p_skipBytes_1_);

    @Override
    public abstract ElecByteBuf writeBoolean(boolean p_writeBoolean_1_);

    @Override
    public abstract ElecByteBuf writeByte(int p_writeByte_1_);

    @Override
    public abstract ElecByteBuf writeShort(int p_writeShort_1_);

    @Override
    public abstract ElecByteBuf writeShortLE(int p_writeShortLE_1_);

    @Override
    public abstract ElecByteBuf writeMedium(int p_writeMedium_1_);

    @Override
    public abstract ElecByteBuf writeInt(int p_writeInt_1_);

    @Override
    public abstract ElecByteBuf writeLong(long p_writeLong_1_);

    @Override
    public abstract ElecByteBuf writeLongLE(long p_writeLongLE_1_);

    @Override
    public abstract ElecByteBuf writeChar(int p_writeChar_1_);

    @Override
    public abstract ElecByteBuf writeFloat(float p_writeFloat_1_);

    @Override
    public abstract ElecByteBuf writeDouble(double p_writeDouble_1_);

    @Override
    public abstract ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_);

    @Override
    public abstract ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_);

    @Override
    public abstract ElecByteBuf writeBytes(ByteBuf p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_);

    @Override
    public abstract ElecByteBuf writeBytes(byte[] p_writeBytes_1_);

    @Override
    public abstract ElecByteBuf writeBytes(byte[] p_writeBytes_1_, int p_writeBytes_2_, int p_writeBytes_3_);

    @Override
    public abstract ElecByteBuf writeBytes(ByteBuffer p_writeBytes_1_);

    @Override
    public abstract ElecByteBuf writeZero(int p_writeZero_1_);

    @Override
    public abstract ElecByteBuf copy();

    @Override
    public abstract ElecByteBuf copy(int p_copy_1_, int p_copy_2_);

    @Override
    public abstract ElecByteBuf setIntLE(int p_setIntLE_1_, int p_setIntLE_2_);

    @Override
    public abstract ElecByteBuf duplicate();

    @Override
    public abstract ElecByteBuf retain(int p_retain_1_);

    @Override
    public abstract ElecByteBuf retain();

    @Override
    public abstract ElecByteBuf touch();

    @Override
    public abstract ElecByteBuf touch(Object hint);

    public interface Factory {

        public ElecByteBuf createByteBuf();

        public ElecByteBuf createByteBuf(ByteBuf parent);

    }

}
