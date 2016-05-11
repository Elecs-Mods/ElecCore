package elec332.core.compat.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by Elec332 on 8-5-2016.
 */
public interface IWailaCapabilityDataProvider<T> {

    public List<String> getWailaBody(List<String> currentTip, T capability, NBTTagCompound tag, EntityPlayer player, RayTraceResult rts,World world, BlockPos pos, TileEntity tile);

    public NBTTagCompound getWailaTag(T capability, EntityPlayerMP player, TileEntity tile, NBTTagCompound tag, World world, BlockPos pos);


}
