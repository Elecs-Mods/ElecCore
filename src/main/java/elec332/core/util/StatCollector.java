package elec332.core.util;

import net.minecraft.util.text.LanguageMap;

/**
 * Created by Elec332 on 10-3-2016.
 * <p>
 * Translation helper
 */
public class StatCollector {

    private static final LanguageMap fallback = LanguageMap.getInstance();

    public static String translateToLocal(String key) {
        return LanguageMap.getInstance().func_230503_a_(key);
    }

    public static String translateToLocalFormatted(String key, Object... format) {
        return String.format(LanguageMap.getInstance().func_230503_a_(key), format);
    }

    public static String translateToFallback(String key) {
        return fallback.func_230503_a_(key);
    }

    public static boolean canTranslate(String key) {
        return LanguageMap.getInstance().func_230506_b_(key);
    }

}
