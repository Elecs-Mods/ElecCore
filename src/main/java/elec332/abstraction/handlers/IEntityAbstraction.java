package elec332.abstraction.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 28-1-2017.
 */
public interface IEntityAbstraction {

    public Entity createEntity(ResourceLocation name, World world);

    public EntityVillager.ITradeList wrap(final Object elecTradeList);

}
