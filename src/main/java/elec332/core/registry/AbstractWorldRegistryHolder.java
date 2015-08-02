package elec332.core.registry;

import com.google.common.collect.Lists;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import elec332.core.util.EventHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

import java.util.List;
import java.util.WeakHashMap;

/**
 * Created by Elec332 on 27-7-2015.
 */
public abstract class AbstractWorldRegistryHolder<T extends IWorldRegistry> {

    public AbstractWorldRegistryHolder(){
        EventHelper.registerHandlerForgeAndFML(this);
    }

    private WeakHashMap<World, T> mappings = new WeakHashMap<World, T>();
    private List<World> worlds = Lists.newArrayList();

    public T get(World world){
        return get(world, true);
    }

    protected T get(World world, boolean create) {
        if (world == null)
            throw new IllegalArgumentException();
        if (!world.isRemote || !serverOnly()) {
            T ret = mappings.get(world);
            if (ret == null && create) {
                ret = newRegistry(world);
                mappings.put(world, ret);
                worlds.add(world);
            }
            return ret;
        }
        return null;
    }

    public abstract boolean serverOnly();

    public abstract T newRegistry(World world);

    //////////////////////////////////////////////////////

    private void unload(World world) {
        mappings.get(world).onWorldUnload();
        mappings.remove(world);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        if (event.phase == TickEvent.Phase.START) {
            T t = get(event.world, false);
            if (t != null)
                t.tick();
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        World world_unload = null;
        for (World world : worlds) {
            if (WorldHelper.getDimID(event.world) == WorldHelper.getDimID(world)) {
                world_unload = world;
                unload(world);
                break;
            }
        }
        if (world_unload != null)
            worlds.remove(world_unload);
    }
}
