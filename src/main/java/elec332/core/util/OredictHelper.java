package elec332.core.util;

import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;

/**
 * Created by Elec332 on 23-12-2014.
 */
@SuppressWarnings({"deprecation", "unused"})
public class OredictHelper {

    private static List<String> allOres = Lists.newArrayList(), allIngots = Lists.newArrayList(), allDusts = Lists.newArrayList();
    private static List<String> allOres_ = Collections.unmodifiableList(allOres), allIngots_ = Collections.unmodifiableList(allIngots), allDusts_ = Collections.unmodifiableList(allDusts);

    public static void initLists() {
    }/* TODO: wait till Forge re-adds this
        allDusts.clear();
        allIngots.clear();
        allOres.clear();
        String[] names = OreDictionary.getOreNames();
        for (String s : names) {
            if (Strings.isNullOrEmpty(s)) { //What?!?
                continue;
            }
            if (s.startsWith("ore")) {
                allOres.add(s);
            } else if (s.startsWith("ingot")) {
                allIngots.add(s);
            } else if (s.startsWith("dust")) {
                allDusts.add(s);
            }
        }
    }

    public static List<String> getAllOres() {
        return allOres_;
    }

    public static List<String> getAllIngots() {
        return allIngots_;
    }

    public static List<String> getAllDusts() {
        return allDusts_;
    }

    public static String concatOreName(String oreName) {
        return oreName.replace("ore", "");
    }

    public static String concatIngotName(String ingotName) {
        return ingotName.replace("ingot", "");
    }

    public static boolean isOre(ItemStack stack) {
        return getOreIDs(stack).length > 0;
    }

    public static List<String> getOreNames(ItemStack stack) {
        List<String> ret = Lists.newArrayList();
        for (int i : getOreIDs(stack)) {
            ret.add(OreDictionary.getOreName(i));
        }
        return ret;
    }

    public static int[] getOreIDs(ItemStack stack) {
        return OreDictionary.getOreIDs(stack);
    }

    public static List<ItemStack> getOres(String name) {
        return getOres(name, false);
    }

    public static List<ItemStack> getOres(String name, boolean alwaysCreateEntry) {
        return OreDictionary.getOres(name, alwaysCreateEntry);
    }*/

}
