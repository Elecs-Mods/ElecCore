package elec332.core.registry;

/**
 * Created by Elec332 on 27-7-2015.
 */
public interface IWorldRegistry {

    /**
     * Gets called every tick
     */
    public void tick();

    /**
     * Gets called when the world unloads, just before it is removed from the registry and made ready for the GC
     */
    public void onWorldUnload();

}
