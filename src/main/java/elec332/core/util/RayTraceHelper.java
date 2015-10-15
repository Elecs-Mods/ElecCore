package elec332.core.util;

import elec332.core.player.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 15-10-2015.
 */
public class RayTraceHelper {

    public static MovingObjectPosition retraceBlock(World world, EntityPlayerMP player, int x, int y, int z) {
        Block block = world.getBlock(x, y, z);
        Vec3 headVec = PlayerHelper.getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLook(1.0F);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3 endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return block.collisionRayTrace(world, x, y, z, headVec, endVec);
    }

}
