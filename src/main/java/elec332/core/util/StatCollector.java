package elec332.core.util;

import net.minecraftforge.fml.common.registry.LanguageRegistry;

/**
 * Created by Elec332 on 10-3-2016.
 */
public class StatCollector {

    public static String translateToLocal(String s){
        return LanguageRegistry.instance().getStringLocalization(s);
    }

}
