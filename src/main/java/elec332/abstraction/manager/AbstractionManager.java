package elec332.abstraction.manager;

import elec332.abstraction.handlers.IAbstractionLayer;
import elec332.abstraction.impl.MCAbstractionHandler;
import elec332.core.main.APIHandler;
import elec332.core.util.MCVersion;
import net.minecraftforge.common.ForgeVersion;

/**
 * Created by Elec332 on 26-1-2017.
 */
@APIHandler.StaticLoad
public class AbstractionManager {

    private static void init(){
        if (init){
            throw new RuntimeException();
        }
        init = true;
        MCVersion mc = MCVersion.getCurrentVersion();
        String packageN;
        if (mc == MCVersion.MC_1_10 || mc == MCVersion.MC_1_10_2){
            packageN = "1_10";
        } else if (mc == MCVersion.MC_1_11){
            packageN = "1_11";
        } else {
            throw new IllegalArgumentException("Unsupported MC version: "+ForgeVersion.mcVersion);
        }
        try {
            AbstractionContainer.abstractionLayer = abs = new MCAbstractionHandler();//(IAbstractionLayer) FMLUtil.loadClass("elec332.abstraction.impl.mc"+packageN+".AbstractionLayer").newInstance();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private static boolean init;
    private static IAbstractionLayer abs;

    static {
        init();
    }

}
