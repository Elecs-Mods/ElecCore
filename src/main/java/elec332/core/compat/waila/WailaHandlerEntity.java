package elec332.core.compat.waila;

import mcp.mobius.waila.api.IEntityComponentProvider;
import mcp.mobius.waila.api.IServerDataProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 19-2-2019
 */
public class WailaHandlerEntity implements IEntityComponentProvider, IServerDataProvider<Entity> {

    @Override
    public void appendServerData(NBTTagCompound data, EntityPlayerMP player, World world, Entity entity) {

    }

}
