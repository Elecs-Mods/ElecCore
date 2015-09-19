package elec332.core.helper;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

/**
 * Created by Elec332 on 23-12-2014.
 */
@SuppressWarnings("deprecation")
public class OredictHelper {

    private static List<String> allOres = Lists.newArrayList();
    private static List<String> allIngots = Lists.newArrayList();

    public static void initLists(){
        for (String s : OreDictionary.getOreNames()){
            if (s.contains("ore")) {
                allOres.add(s);
            } else if (s.contains("ingot")) {
                allIngots.add(s);
            }
        }
    }

    public static List<String> getAllOres() {
        return ImmutableList.copyOf(allOres);
    }

    public static List<String> getAllIngots() {
        return ImmutableList.copyOf(allIngots);
    }

    public static String concatOreName(String oreName){
        return oreName.replace("ore", "");
    }

    public static String concatIngotName(String ingotName){
        return ingotName.replace("ingot", "");
    }

    public static boolean oreExists(ItemStack stack){
        return OreDictionary.getOres(getOreName(stack)).size() > 0;
    }

    public static String getOreName(ItemStack stack){
        return OreDictionary.getOreName(getOreID(stack));
    }

    public static int getOreID(ItemStack stack){
        return OreDictionary.getOreID(stack);
    }

    static {
        initLists();
    }


    /**
     * Deprecated
     */
    @Deprecated
    public static Item getFirstOredictEntry(String Oredictname){
        return getOredictEntry(0, Oredictname);
    }

    @Deprecated
    public static Item getOredictEntry(int number, String Oredictname){
        return OreDictionary.getOres(Oredictname).get(number).getItem();
    }

    @Deprecated
    public static int getFirstOredictItemDamage(String Oredictname){
        return getOredictItemDamage(0, Oredictname);
    }

    @Deprecated
    public static int getOredictItemDamage(int number, String OreDictname){
        return OreDictionary.getOres(OreDictname).get(number).getItemDamage();
    }

    @Deprecated
    public static ItemStack getFirstOreDictItemWithMeta(String Oredictname){
        return getOreDictItemWithMeta(Oredictname, 0);
    }

    @Deprecated
    public static ItemStack getOreDictItemWithMeta(String Oredictname, int entry){
        return new ItemStack(getOredictEntry(entry, Oredictname), 1, getOredictItemDamage(entry, Oredictname));
    }

}
