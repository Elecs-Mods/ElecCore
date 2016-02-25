package elec332.core.loader;

/**
 * Created by Elec332 on 18-11-2015.
 */
public class ElecCoreLoader{}/* implements IFMLLoadingPlugin {

    public ElecCoreLoader(){
        if (logger == null){
            logger = LogManager.getLogger("ElecCoreLoader");
        }
    }

    public static Logger logger;
    public static boolean isObfuscated;

    /**
     * Return a list of classes that implements the IClassTransformer interface
     *
     * @return a list of classes that implements the IClassTransformer interface
     *
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                //ASMLoader.class.getCanonicalName()
        };
    }

    /**
     * Return a class name that implements "ModContainer" for injection into the mod list
     * The "getName" function should return a name that other mods can, if need be,
     * depend on.
     * Trivially, this modcontainer will be loaded before all regular mod containers,
     * which means it will be forced to be "immutable" - not susceptible to normal
     * sorting behaviour.
     * All other mod behaviours are available however- this container can receive and handle
     * normal loading events
     *
    @Override
    public String getModContainerClass() {
        return null;
    }

    /**
     * Return the class name of an implementor of "IFMLCallHook", that will be run, in the
     * main thread, to perform any additional setup this coremod may require. It will be
     * run <strong>prior</strong> to Minecraft starting, so it CANNOT operate on minecraft
     * itself. The game will deliberately crash if this code is detected to trigger a
     * minecraft class loading (TODO: implement crash ;) )
     *
    @Override
    public String getSetupClass() {
        return null;
    }

    /**
     * Inject coremod data into this coremod
     * This data includes:
     * "mcLocation" : the location of the minecraft directory,
     * "coremodList" : the list of coremods
     * "coremodLocation" : the file this coremod loaded from,
     *
     * @param data
     *
    @Override
    public void injectData(Map<String, Object> data) {
        isObfuscated = !((Boolean) data.get("runtimeDeobfuscationEnabled"));
    }

    /**
     * Return an optional access transformer class for this coremod. It will be injected post-deobf
     * so ensure your ATs conform to the new srgnames scheme.
     *
     * @return the name of an access transformer class or null if none is provided
     *
    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}*/
