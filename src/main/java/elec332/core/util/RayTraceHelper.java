package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 15-10-2015.
 */
public class RayTraceHelper {

    public static MovingObjectPosition retraceBlock(World world, EntityPlayerMP player, BlockPos pos) {
        Block block = WorldHelper.getBlockAt(world, pos);
        Vec3 headVec = PlayerHelper.getCorrectedHeadVec(player);
        Vec3 lookVec = player.getLook(1.0F);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3 endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return block.collisionRayTrace(world, pos, headVec, endVec);
    }

}
