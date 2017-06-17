package elec332.core.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 30-1-2017.
 */
public class EntityHelper {

    public static Entity createEntity(ResourceLocation name, World world) {
        return EntityList.createEntityByIDFromName(new ResourceLocation(name.toString().toLowerCase()), world);
    }

}
