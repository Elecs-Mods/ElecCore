package elec332.abstraction.handlers;

import elec332.core.world.IElecWorldEventListener;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 26-1-2017.
 */
public interface IWorldAbstraction {

    public void unRegisterWorldEventListener(IElecWorldEventListener listener);

    public void registerWorldEventListener(IElecWorldEventListener listener);

    public IBlockState getBlockStateForPlacement(Block block, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, @Nonnull EnumHand hand);

    public boolean canBlockBePlaced(World world, Block block, BlockPos pos, boolean b, EnumFacing facing, @Nullable Entity entity);

    public void notifyNeighborsOfStateChange(World world, BlockPos pos, Block block);

}
