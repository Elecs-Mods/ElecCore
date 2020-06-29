package elec332.core.util;

import net.minecraft.util.text.LanguageMap;

/**
 * Created by Elec332 on 10-3-2016.
 * <p>
 * Translation helper
 */
public class StatCollector {

    private static LanguageMap fallback = new LanguageMap();

    public static String translateToLocal(String key) {
        return LanguageMap.getInstance().translateKey(key);
    }

    public static String translateToLocalFormatted(String key, Object... format) {
        return String.format(LanguageMap.getInstance().translateKey(key), format);
    }

    public static String translateToFallback(String key) {
        return fallback.translateKey(key);
    }

    public static boolean canTranslate(String key) {
        return LanguageMap.getInstance().exists(key);
    }

}
