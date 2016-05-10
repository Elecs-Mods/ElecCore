package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
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

    public static RayTraceResult rayTrace(EntityPlayer player, double distance){
        Vec3d vec3d = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d vec3d1 = getVectorForRotation(player.rotationPitch, player.rotationYawHead);
        Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * distance, vec3d1.yCoord * distance, vec3d1.zCoord * distance);
        return player.worldObj.rayTraceBlocks(vec3d, vec3d2, false, false, true);
    }

    //Because this is protected in Entity -_-
    private static Vec3d getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float)Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float)Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3d((double)(f1 * f2), (double)f3, (double)(f * f2));
    }

}
