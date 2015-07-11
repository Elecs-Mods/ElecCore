package elec332.core.minetweaker;

import cpw.mods.fml.common.registry.GameData;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Elec332 on 28-5-2015.
 */
public class MineTweakerHelper {

    public static StringBuilder newStringBuilder(){
        return new StringBuilder();
    }

    public static String getItemStack(ItemStack stack, boolean stackSize){
        StringBuilder builder = newStringBuilder();
        if (stack == null || stack.getItem() == null)
            return "null";
        builder.append("<");
        builder.append(getItemRegistryName(stack));
        if (stack.getItemDamage() > 0) {
            builder.append(":");
            if (stack.getItemDamage() == OreDictionary.WILDCARD_VALUE)
                builder.append("*");
            else builder.append(stack.getItemDamage());
        }
        builder.append(">");
        if (stack.stackSize > 0 && stackSize){
            builder.append(" * ");
            builder.append(stack.stackSize);
        }
        return builder.toString();
    }

    public static String getItemRegistryName(ItemStack stack){
        return GameData.getItemRegistry().getNameForObject(stack.getItem());
    }

    public static void reloadMineTweakerScripts(){
        try {
            Class.forName("minetweaker.MineTweakerImplementationAPI").getDeclaredMethod("reload").invoke(null);
        } catch (Throwable t){
            //Nope, no reload for you
        }
    }

    public static File getMTFile(String fileName, String... fileComments){
        File ret = new File("scripts/Elec332/scripts", fileName+".zs");
        if (!ret.getParentFile().exists())
            ret.getParentFile().mkdirs();
        if (!ret.exists()){
            try {
                ret.createNewFile();
                PrintWriter writer = new PrintWriter(ret);
                for (String s : fileComments)
                    writer.println(s);
                writer.close();
            } catch (IOException e){
                throw new RuntimeException("IOException while attempting to create a new MT file", e);
            }
        }
        return ret;
    }
}
