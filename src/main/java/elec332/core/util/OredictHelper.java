package elec332.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import elec332.core.main.ElecCore;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Created by Elec332 on 23-12-2014.
 */
@SuppressWarnings({"deprecation", "unused"})
public class OredictHelper {

    private static List<String> allOres = Lists.newArrayList();
    private static List<String> allIngots = Lists.newArrayList();
    private static List<String> allDusts = Lists.newArrayList();

    public static void initLists(){
        allDusts.clear();
        allIngots.clear();
        allOres.clear();
        String[] names = OreDictionary.getOreNames();
        for (int i = 0; i < names.length; i++) {
            String s = names[i];
            if (s == null){ //What?!?
                ElecCore.systemPrintDebug("Null ore for ID: "+i);
                continue;
            }
            if (s.startsWith("ore")) {
                allOres.add(s);
            } else if (s.startsWith("ingot")) {
                allIngots.add(s);
            } else if (s.startsWith("dust")){
                allDusts.add(s);
            }
        }
    }

    public static List<String> getAllOres() {
        return ImmutableList.copyOf(allOres);
    }

    public static List<String> getAllIngots() {
        return ImmutableList.copyOf(allIngots);
    }

    public static List<String> getAllDusts(){
        return ImmutableList.copyOf(allDusts);
    }

    public static String concatOreName(String oreName){
        return oreName.replace("ore", "");
    }

    public static String concatIngotName(String ingotName){
        return ingotName.replace("ingot", "");
    }

    public static boolean isOre(ItemStack stack){
        return getOreIDs(stack).length > 0;
    }

    public static List<String> getOreNames(ItemStack stack){
        List<String> ret = Lists.newArrayList();
        for (int i : getOreIDs(stack)){
            ret.add(OreDictionary.getOreName(i));
        }
        return ret;
    }

    public static int[] getOreIDs(ItemStack stack){
        return OreDictionary.getOreIDs(stack);
    }

    public static MinecraftList<ItemStack> getOres(String name, boolean alwaysCreateEntry){
        return new MinecraftList<ItemStack>(OreDictionary.getOres(name, alwaysCreateEntry));
    }

    static {
        initLists();
    }

}
