package elec332.core.json;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.minetweaker.MineTweakerHelper;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;

/**
 * Created by Elec332 on 11-7-2015.
 */
public class ExtraTypeAdapters {
    public static final JsonHandler.ElecFactory[] allFactories = new JsonHandler.ElecFactory[]{
            new JsonHandler.ElecFactory<ItemStack>() {
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
            }
    };
}
