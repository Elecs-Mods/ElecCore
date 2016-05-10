package elec332.core.compat;

import com.google.common.collect.Maps;
import elec332.core.compat.handlers.IWailaCapabilityDataProvider;
import elec332.core.util.AbstractCompatHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.Map;

/**
 * Created by Elec332 on 15-8-2015.
 */
public class ElecCoreCompatHandler extends AbstractCompatHandler {

    public ElecCoreCompatHandler(Configuration config, Logger logger) {
        super(config, logger);
    }

    /**
     * Use this to load EG a static boolean checking if a certain mod is loaded
     */
    @Override
    public void loadList() {
        NEI = compatEnabled("NotEnoughItems");
        WAILA = compatEnabled("Waila");

        FMLInterModComms.sendMessage("Waila", "register", "elec332.core.compat.handlers.WailaCompatHandler.register");
    }

    public static boolean NEI;
    public static boolean WAILA;

    private static Map<Capability, IWailaCapabilityDataProvider> dataProviders_;
    public static final Map<Capability, IWailaCapabilityDataProvider> dataProviders;

    public static <T> void registerCapabilityDataProvider(Capability<T> capability, IWailaCapabilityDataProvider<T> dataProvider){
        dataProviders_.put(capability, dataProvider);
    }

    static {
        dataProviders_ = Maps.newHashMap();
        dataProviders = Collections.unmodifiableMap(dataProviders_);
    }

}
