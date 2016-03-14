package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Created by Elec332 on 15-10-2015.
 */
public class RayTraceHelper {

    public static RayTraceResult retraceBlock(World world, EntityPlayerMP player, BlockPos pos) {
        IBlockState block = WorldHelper.getBlockState(world, pos);
        Vec3d headVec = PlayerHelper.getCorrectedHeadVec(player);
        Vec3d lookVec = player.getLook(1.0F);
        double reach = PlayerHelper.getBlockReachDistance(player);
        Vec3d endVec = headVec.addVector(lookVec.xCoord * reach, lookVec.yCoord * reach, lookVec.zCoord * reach);
        return block.getBlock().collisionRayTrace(block, world, pos, headVec, endVec);
    }

}
