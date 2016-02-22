package elec332.core.nbt;

import com.google.common.base.Function;
import net.minecraft.nbt.*;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 18-2-2016.
 */
public class NBTMap<K, V> extends HashMap<K, V> implements INBTSerializable<NBTTagList> {

    public static <K extends INBTSerializable, V extends INBTSerializable> NBTMap<K, V> newNBTMap(@Nonnull Class<K> kClazz, @Nonnull Callable<K> kCallable, @Nonnull Class<V> vClazz, @Nonnull Function<K, V> vCallable){
        return newNBTMap_(kClazz, kCallable, vClazz, vCallable);
    }

    public static <K, V extends INBTSerializable> NBTMap<K, V> newNBTMap(@Nonnull Class<K> kClazz, @Nonnull Class<V> vClazz, @Nonnull Function<K, V> vCallable){
        return newNBTMap_(kClazz, null, vClazz, vCallable);
    }

    public static <K extends INBTSerializable, V> NBTMap<K, V> newNBTMap(@Nonnull Class<K> kClazz, @Nonnull Callable<K> kCallable, @Nonnull Class<V> vClazz){
        return newNBTMap_(kClazz, kCallable, vClazz, null);
    }

    public static <K, V> NBTMap<K, V> newNBTMap(@Nonnull Class<K> kClazz, @Nonnull Class<V> vClazz){
        return newNBTMap_(kClazz, null, vClazz, null);
    }

    private static <K, V> NBTMap<K, V> newNBTMap_(@Nonnull Class<K> kClazz, Callable<K> kCallable, @Nonnull Class<V> vClazz, Function<K, V> vCallable){
        if (!isValidNBT(kClazz)) {
            boolean b = INBTSerializable.class.isAssignableFrom(kClazz);
            if ((b && kCallable == null || !b) && !NBTBase.class.isAssignableFrom(kClazz)){
                throw new IllegalArgumentException();
            }
        }
        if (!isValidNBT(vClazz)){
            boolean b = INBTSerializable.class.isAssignableFrom(vClazz);
            if ((b && vCallable == null || !b) && !NBTBase.class.isAssignableFrom(vClazz)){
                throw new IllegalArgumentException();
            }
        }
        return new NBTMap<K, V>(kClazz, kCallable, vClazz, vCallable);
    }

    private NBTMap(Class<K> kClazz, Callable<K> kCallable, Class<V> vClass, Function<K, V> vCallable){
        this.kClass = kClazz;
        this.vClass = vClass;
        this.kNBT = kCallable != null;
        this.vNBT = vCallable != null;
        this.kCallable = kCallable;
        this.vCallable = vCallable;
        this.serializeNull = false;
    }

    private final Class<K> kClass;
    private final Class<V> vClass;

    private boolean serializeNull;

    private final boolean kNBT, vNBT;
    private final Callable<K> kCallable;
    private final Function<K, V> vCallable;

    public NBTMap<K, V> setSerializeNull(boolean serializeNull){
        this.serializeNull = serializeNull;
        return this;
    }

    @Override
    public NBTTagList serializeNBT() {
        NBTTagList ret = new NBTTagList();
        for (final Map.Entry<K, V> entry : entrySet()){
            if (entry.getValue() == null && !serializeNull){
                continue;
            }
            NBTTagCompound tag = new NBTTagCompound();
            writeToNBT(tag, "k", entry.getKey());
            writeToNBT(tag, "v", entry.getValue());
            ret.appendTag(tag);
        }
        return ret;
    }

    @Override
    @SuppressWarnings("all")
    public void deserializeNBT(final NBTTagList nbt){
        try {
            clear();
            for (int i = 0; i < nbt.tagCount(); i++) {
                NBTTagCompound tag = nbt.getCompoundTagAt(i);
                K k;
                V v;
                if (kNBT) {
                    k = kCallable.call();
                    ((INBTSerializable)k).deserializeNBT(tag.getTag("k"));
                } else {
                    k = readFromNBT(tag, "k", kClass);
                }
                if (vNBT){
                    v = vCallable.apply(k);
                    ((INBTSerializable)v).deserializeNBT(tag.getTag("v"));
                } else {
                    v = readFromNBT(tag, "v", vClass);
                }
                put(k, v);
            }
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private void writeToNBT(NBTTagCompound tag, String name, Object toWrite){
        NBTBase serialized;
        if (toWrite instanceof NBTBase){
            serialized = (NBTBase) toWrite;
        } else if (toWrite instanceof INBTSerializable){
            serialized = ((INBTSerializable) toWrite).serializeNBT();
        } else if (toWrite instanceof String){
            serialized = new NBTTagString((String) toWrite);
        } else if (toWrite instanceof UUID) {
            serialized = new NBTTagString(toWrite.toString());
        } else if (toWrite.getClass().equals(Byte.TYPE)){
            serialized = new NBTTagByte((Byte) toWrite);
        } else if (toWrite.getClass().equals(Short.TYPE)){
            serialized = new NBTTagShort((Short) toWrite);
        } else if (toWrite.getClass().equals((Integer.TYPE))){
            serialized = new NBTTagInt((Integer) toWrite);
        }



        else {
            throw new IllegalArgumentException();
        }
        if (serialized != null){
            tag.setTag(name, serialized);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T readFromNBT(NBTTagCompound tag, String name, Class<T> type){
        NBTBase data = tag.getTag(name);
        if (data == null) {
            return null;
        } else if (NBTBase.class.isAssignableFrom(type)){
            return (T) data;
        } else if (type == String.class){
            return (T) ((NBTTagString)data).getString();
        } else if (type == UUID.class){
            return (T) UUID.fromString(((NBTTagString)data).getString());
        } else if (type.equals(Byte.TYPE)){
            return (T) (Byte) ((NBTBase.NBTPrimitive)data).getByte();
        } else if (type.equals(Short.TYPE)){
            return (T) (Short) ((NBTBase.NBTPrimitive)data).getShort();
        } else if (type.equals((Integer.TYPE))){
            return (T) (Integer) ((NBTBase.NBTPrimitive)data).getInt();
        }


        throw new IllegalArgumentException();
    }

    private static boolean isValidNBT(Class clazz){
        return clazz == String.class || clazz == UUID.class || clazz == Byte.class || clazz == Short.class || clazz == Integer.class;// || clazz == Float.class || clazz == Double.class || clazz == Long.class || clazz == Byte[].class || clazz == Integer[].class;
    }

}
