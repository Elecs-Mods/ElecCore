package elec332.core.compat.waila;

import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaHandlerEntity implements IEntityComponentProvider, IServerDataProvider<Entity> {

    @Override
    public void appendServerData(CompoundNBT data, ServerPlayerEntity player, World world, Entity entity) {

    }

}
