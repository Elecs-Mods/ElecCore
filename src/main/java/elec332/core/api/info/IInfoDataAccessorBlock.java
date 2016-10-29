package elec332.core.api.info;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 16-10-2016.
 */
public interface IInfoDataAccessorBlock extends IInfoDataAccessor {

    @Nonnull
    @Override
    public EntityPlayer getPlayer();

    @Nonnull
    @Override
    public World getWorld();

    @Nonnull
    public BlockPos getPos();

    @Nonnull
    @Override
    public NBTTagCompound getData();

    @Override
    public Vec3d getHitVec();

    @Nonnull
    public EnumFacing getSide();

    @Nonnull
    public IBlockState getBlockState();

    @Nonnull
    public Block getBlock();

    public TileEntity getTileEntity();

    public ItemStack getStack();

    public RayTraceResult getRayTraceResult();

}
