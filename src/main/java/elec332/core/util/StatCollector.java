package elec332.core.util;

import elec332.core.api.annotations.StaticLoad;
import net.minecraft.locale.Language;

/**
 * Created by Elec332 on 10-3-2016.
 * <p>
 * Translation helper
 */
@StaticLoad
public class StatCollector {

    private static final Language fallback = Language.getInstance();

    public static String translateToLocal(String key) {
        return Language.getInstance().getOrDefault(key);
    }

    public static String translateToLocalFormatted(String key, Object... format) {
        return String.format(Language.getInstance().getOrDefault(key), format);
    }

    public static String translateToFallback(String key) {
        return fallback.getOrDefault(key);
    }

    public static boolean canTranslate(String key) {
        return Language.getInstance().has(key);
    }

}
