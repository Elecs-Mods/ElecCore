package elec332.core.json;

import com.google.common.collect.Lists;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.minetweaker.MineTweakerHelper;
import elec332.core.util.NBT;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import java.io.IOException;
import java.util.List;

import static elec332.core.util.NBT.NBTData.*;

/**
 * Created by Elec332 on 11-7-2015.
 */
public class ExtraTypeAdapters {

    public static final JsonHandler.ElecFactory<ItemStack> itemStack = new JsonHandler.ElecFactory<ItemStack>() {
        @Override
        public TypeAdapter<ItemStack> getTypeAdapter() {
            return new TypeAdapter<ItemStack>() {
                @Override
                public void write(JsonWriter out, ItemStack value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.beginObject();
                    out.name("name").value(MineTweakerHelper.getItemRegistryName(value));
                    out.name("count").value(value.stackSize);
                    out.name("damage").value(Items.diamond.getDamage(value));
                    out.name("nbtTag");
                    JsonHandler.getGson().toJson(value.getTagCompound(), NBTTagCompound.class, out);
                    out.endObject();
                }

                @Override
                public ItemStack read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    String name = "";
                    int count = 1;
                    int damage = 0;
                    NBTTagCompound tag = null;
                    while (in.hasNext()){
                        String inName = in.nextName();
                        if (inName.equals("name")) {
                            name = in.nextString();
                        } else if (inName.equals("count")) {
                            count = in.nextInt();
                        } else if (inName.equals("damage")) {
                            damage = in.nextInt();
                        } else if (inName.equals("nbtTag")) {
                            tag = JsonHandler.getGson().fromJson(in, NBTTagCompound.class);
                        }
                    }
                    in.endObject();
                    ItemStack ret = new ItemStack(GameRegistry.findItem(name.replace(":", " ").split(" ")[0], name.replace(":", " ").split(" ")[1]), count, damage);
                    ret.setTagCompound(tag);
                    return ret;
                }
            };
        }

        @Override
        public Class<ItemStack> getFactoryClass() {
            return ItemStack.class;
        }
    };

    public static final JsonHandler.ElecFactory<NBTTagCompound> nbtTagCompound = new JsonHandler.ElecFactory<NBTTagCompound>() {
        @Override
        public TypeAdapter<NBTTagCompound> getTypeAdapter() {
            return new TypeAdapter<NBTTagCompound>() {
                @Override
                public void write(JsonWriter out, NBTTagCompound value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    NBTTagCompound tagCompound = (NBTTagCompound)value.copy();
                    out.beginObject();
                    for (Object obj : tagCompound.func_150296_c()){
                        String s = (String) obj;
                        out.name(s);
                        handleOut(out, tagCompound.getTag(s).getId(), tagCompound.getTag(s));
                    }
                    //out.name(END.toString()).value(END.toString());
                    out.endObject();
                }

                @Override
                public NBTTagCompound read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    NBTTagCompound ret = new NBTTagCompound();
                    in.beginObject();
                    while (in.hasNext()){
                        String toAdd = in.nextName();
                        NBTBase tagToAdd;
                        in.beginObject();
                        tagToAdd = handleIn(in, NBT.NBTData.valueOf(in.nextName()));
                        in.endObject();
                        ret.setTag(toAdd, tagToAdd);
                    }
                    in.endObject();
                    return ret;
                }

                private NBTBase handleIn(JsonReader in, NBT.NBTData type) throws IOException {
                    switch (type){
                        case BYTE:
                            return new NBTTagByte((byte) in.nextInt());
                        case SHORT:
                            return new NBTTagShort((short) in.nextInt());
                        case INT:
                            return new NBTTagInt(in.nextInt());
                        case LONG:
                            return new NBTTagLong(in.nextLong());
                        case FLOAT:
                            return new NBTTagFloat((float) in.nextDouble());
                        case DOUBLE:
                            return new NBTTagDouble(in.nextDouble());
                        case BYTE_ARRAY:
                            return new NBTTagByteArray((byte[]) JsonHandler.getGson().fromJson(in, byte[].class));
                        case STRING:
                            return new NBTTagString(in.nextString());
                        case LIST:
                            List<NBTBase> nbtList = Lists.newArrayList();
                            in.beginObject();
                            //in.nextName();
                            //int tag = in.nextInt();
                            in.nextName();
                            in.beginArray();
                            while (in.hasNext()){
                                in.beginObject();
                                nbtList.add(handleIn(in, NBT.NBTData.valueOf(in.nextName())));
                                in.endObject();
                            }
                            in.endArray();
                            in.endObject();
                            NBTTagList tagList = new NBTTagList();
                            for (NBTBase nbtBase : nbtList)
                                tagList.appendTag(nbtBase);
                            return tagList;
                        case COMPOUND:
                            return read(in);
                        case INT_ARRAY:
                            return new NBTTagIntArray((int[]) JsonHandler.getGson().fromJson(in, int[].class));
                        case END:
                            crash();
                            return null;
                        default:
                            crash();
                            return null;
                    }
                }


                private void handleOut(JsonWriter out, byte i, NBTBase nbtBase) throws IOException {
                    out.beginObject();
                    //out.name("id").value(i);
                    if (BYTE.equals(i)){
                        out.name(BYTE.toString()).value(((NBTTagByte)nbtBase).func_150290_f());
                    } else if (SHORT.equals(i)){
                        out.name(SHORT.toString()).value(((NBTTagShort)nbtBase).func_150289_e());
                    } else if (INT.equals(i)){
                        out.name(INT.toString()).value(((NBTTagInt)nbtBase).func_150287_d());
                    } else if (LONG.equals(i)){
                        out.name(LONG.toString()).value(((NBTTagLong)nbtBase).func_150291_c());
                    } else if (FLOAT.equals(i)){
                        out.name(FLOAT.toString()).value(((NBTTagFloat)nbtBase).func_150288_h());
                    } else if (DOUBLE.equals(i)){
                        out.name(DOUBLE.toString()).value(((NBTTagDouble)nbtBase).func_150286_g());
                    } else if (BYTE_ARRAY.equals(i)){
                        out.name(BYTE_ARRAY.toString());
                        JsonHandler.getGson().toJson(((NBTTagByteArray)nbtBase).func_150292_c(), byte[].class, out);
                    } else if (STRING.equals(i)){
                        out.name(STRING.toString()).value(((NBTTagString)nbtBase).func_150285_a_());
                    } else if (LIST.equals(i)){
                        NBTTagList tagList = (NBTTagList) nbtBase;
                        out.name(LIST.toString());
                        out.beginObject();
                        //out.name("tagType").value(tagList.func_150303_d());
                        out.name("contents");
                        out.beginArray();
                        for (int j = 0; j < tagList.tagCount(); j++) { //TODO: Improve efficiency
                            NBTBase nbt;
                            nbt = ((NBTTagList)tagList.copy()).removeTag(j);
                            //tagList.func_150304_a(j, nbt = tagList.removeTag(j));
                            handleOut(out, nbt.getId(), nbt);
                        }
                        out.endArray();
                        out.endObject();
                    } else if (COMPOUND.equals(i)){
                        out.name(COMPOUND.toString());
                        write(out, (NBTTagCompound) nbtBase);
                    } else if (INT_ARRAY.equals(i)){
                        out.name(INT_ARRAY.toString());
                        JsonHandler.getGson().toJson(((NBTTagIntArray)nbtBase).func_150302_c(), int[].class, out);
                    }
                    out.endObject();
                }

            };
        }

        @Override
        public Class<NBTTagCompound> getFactoryClass() {
            return NBTTagCompound.class;
        }
    };

    public static final JsonHandler.ElecFactory<Fluid> fluid = new JsonHandler.ElecFactory<Fluid>() {
        @Override
        public TypeAdapter<Fluid> getTypeAdapter() {
            return new TypeAdapter<Fluid>() {
                @Override
                public void write(JsonWriter out, Fluid value) throws IOException {
                    if (value == null) {
                        out.nullValue();
                        return;
                    }
                    out.beginObject();
                    out.name("name").value(value.getName());
                    out.endObject();
                }

                @Override
                public Fluid read(JsonReader in) throws IOException {
                    if (in.peek() == JsonToken.NULL) {
                        in.nextNull();
                        return null;
                    }
                    in.beginObject();
                    Fluid ret = null;
                    if (in.nextName().equals("name"))
                        ret = FluidRegistry.getFluid(in.nextString());
                    in.endObject();
                    return ret;
                }
            };
        }

        @Override
        public Class<Fluid> getFactoryClass() {
            return Fluid.class;
        }
    };

    @SuppressWarnings("unchecked")
    public static final List<JsonHandler.ElecFactory<?>> allFactories = Lists.newArrayList(
            itemStack, nbtTagCompound, fluid
    );

    private static void crash(){
        throw new RuntimeException();
    }

}
