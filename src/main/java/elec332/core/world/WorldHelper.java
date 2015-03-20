package elec332.core.world;

import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 20-3-2015.
 */
public class WorldHelper {

    public static void spawnLightningAt(World world, double x, double y, double z){
        world.playSoundEffect(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
    }
}
