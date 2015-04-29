package elec332.core.world;

import elec332.core.player.PlayerHelper;
import elec332.core.util.BlockLoc;
import net.minecraft.block.Block;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 20-3-2015.
 */
public class WorldHelper {

    public static TileEntity getTileAt(World world, BlockLoc loc){
        return world.getTileEntity(loc.xCoord, loc.yCoord, loc.zCoord);
    }

    public static Block getBlockAt(World world, BlockLoc loc){
        return world.getBlock(loc.xCoord, loc.yCoord, loc.zCoord);
    }

    public static void spawnLightningAtLookVec(EntityPlayer player, Double range){
        MovingObjectPosition position = PlayerHelper.getPosPlayerIsLookingAt(player, range);
        spawnLightningAt(player.worldObj, position.blockX, position.blockY, position.blockZ);
    }

    public static void spawnLightningAt(World world, double x, double y, double z){
        world.playSoundEffect(x, y, z,"ambient.weather.thunder", 10000.0F, 0.8F);
        world.playSoundEffect(x, y, z,"random.explode", 10000.0F, 0.8F);
        world.spawnEntityInWorld(new EntityLightningBolt(world, x, y, z));
    }
}
