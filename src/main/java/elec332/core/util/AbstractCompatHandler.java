package elec332.core.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.ModAPIManager;
import cpw.mods.fml.common.ModContainer;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Created by Elec332 on 5-8-2015.
 */
public abstract class AbstractCompatHandler {

    public AbstractCompatHandler(Configuration config, Logger logger){
        if (config != null && addCategoryComment())
            config.getCategory(getCompatCategory()).setComment("Sets if compat for the mod will be enabled, FALSE = always disabled, AUTO = enabled if mod is loaded, TRUE = always enabled (Not recommended, will crash if said mod isn't loaded)");
        this.configuration = config;
        this.logger = logger;
        this.compatHandlers = Lists.newArrayList();
        this.locked = false;
        loadList();
    }

    protected final Configuration configuration;
    private final Logger logger;
    private List<ICompatHandler> compatHandlers;
    private boolean locked;
    protected static final String[] possibleOptions = new String[]{CompatEnabled.FALSE.toString(), CompatEnabled.AUTO.toString(), CompatEnabled.TRUE.toString()};

    public void addHandler(ICompatHandler handler){
        if (locked)
            throw new RuntimeException("A mod attempted to register a CompatHandler too late!");
        compatHandlers.add(handler);
    }

    public String getCompatCategory(){
        return "compat";
    }

    public boolean addCategoryComment(){
        return true;
    }

    protected List<ICompatHandler> getCompatHandlers(){
        return ImmutableList.copyOf(compatHandlers);
    }

    protected ICompatHandler forMod(String mod){
        for (ICompatHandler compatHandler : compatHandlers){
            if (compatHandler.getName().equals(mod))
                return compatHandler;
        }
        return null;
    }

    public void init(){
        locked = true;
        for (ICompatHandler handler : compatHandlers){
            if (compatEnabled(handler.getType(), handler.compatEnabled(), handler.getName())) {
                logger.info(getLoadingMessage(handler));
                handler.init();
            } else {
                logger.info(getHandlerNotLoaded(handler));
            }
        }
    }

    protected String getLoadingMessage(ICompatHandler handler){
        return "Loading compat handler for: "+handler.getName();
    }

    protected String getHandlerNotLoaded(ICompatHandler handler){
        return handler.getName()+" was not detected, skipping compat handler for it.";
    }

    /**
     * Use this to load EG a static boolean checking if a certain mod is loaded
     */
    public abstract void loadList();


    ///////////////////////////////////////////////////////////////
    protected final boolean compatEnabled(String name){
        return compatEnabled(name, ModType.MOD);
    }

    protected final boolean compatEnabled(String name, ModType type){
        return compatEnabled(type, CompatEnabled.AUTO, name);
    }

    protected final boolean compatEnabled(ModType type, CompatEnabled enabled, String name){
        switch (isConfigEnabled(name, enabled)){
            case FALSE:
                return false;
            case AUTO:
                break;
            case TRUE:
                return true;
        }
        if (type == ModType.API)
            return isAPILoaded(name);
        else
            return Loader.isModLoaded(name);
    }

    protected final CompatEnabled isConfigEnabled(String name, CompatEnabled def){
        CompatEnabled enabled = getIsCompatEnabled(name, def);
        return enabled == null ? CompatEnabled.AUTO : enabled;
    }

    protected CompatEnabled getIsCompatEnabled(String mod, CompatEnabled defaultValue){
        return configuration != null ? CompatEnabled.valueOf(configuration.getString(mod, getCompatCategory(), defaultValue.toString(), "", possibleOptions)) : CompatEnabled.AUTO;
    }

    protected static boolean isAPILoaded(String name){
        for (ModContainer API : ModAPIManager.INSTANCE.getAPIList()){
            if (API.getModId().equals(name))
                return true;
        }
        return false;
    }

    public enum CompatEnabled{
        FALSE, AUTO, TRUE
    }

    public enum ModType{
        MOD, API
    }

    public static abstract class ICompatHandler{

        public ModType getType(){
            return ModType.MOD;
        }

        public CompatEnabled compatEnabled(){
            return CompatEnabled.AUTO;
        }

        public abstract String getName();

        public abstract void init();

    }
}
