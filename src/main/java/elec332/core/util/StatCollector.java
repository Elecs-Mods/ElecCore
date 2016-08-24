package elec332.core.util;

import net.minecraft.util.text.translation.I18n;

/**
 * Created by Elec332 on 10-3-2016.
 */
@SuppressWarnings("deprecation")
public class StatCollector {

    public static String translateToLocal(String key) {
        return I18n.translateToLocal(key);
    }

    public static String translateToLocalFormatted(String key, Object... format) {
        return I18n.translateToLocalFormatted(key, format);
    }

    public static String translateToFallback(String key) {
        return I18n.translateToFallback(key);
    }

    public static boolean canTranslate(String key) {
        return I18n.canTranslate(key);
    }

}
