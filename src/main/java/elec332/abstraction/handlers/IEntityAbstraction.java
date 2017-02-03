package elec332.abstraction.handlers;

import elec332.core.util.IElecTradeList;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 28-1-2017.
 */
public interface IEntityAbstraction {

    public Entity createEntity(ResourceLocation name, World world);

    public EntityVillager.ITradeList wrap(final IElecTradeList tradeList);

}
