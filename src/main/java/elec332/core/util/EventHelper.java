package elec332.core.util;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

/**
 * Created by Elec332 on 12-5-2015.
 */
@Deprecated
public class EventHelper {

    public static void registerHandler(Handler side, Object reg){
        switch (side){
            case FORGE:
                registerHandlerForge(reg);
                break;
            case FML:
                registerHandlerFML(reg);
                break;
            case BOTH:
                registerHandlerForgeAndFML(reg);
                break;
            default:
                break;
        }
    }

    public static void registerHandlerForgeAndFML(Object reg){
        registerHandlerForge(reg);
       // registerHandlerFML(reg);
    }

    public static void registerHandlerForge(Object reg){
        MinecraftForge.EVENT_BUS.register(reg);
    }

    public static void registerHandlerFML(Object reg){
        FMLCommonHandler.instance().bus().register(reg);
    }

    public enum Handler{
        FML, FORGE, BOTH
    }
}
