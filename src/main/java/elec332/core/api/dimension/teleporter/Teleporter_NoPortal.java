package elec332.core.api.dimension.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

/**
 * Created by Elec332 on 24-1-2015.
 */
public class Teleporter_NoPortal extends Teleporter {
    public Teleporter_NoPortal(WorldServer world){
        super(world);
    }

    @Override
    public void placeInPortal(Entity entity, double par2, double par4, double par6, float par8)
    {

    }
}
