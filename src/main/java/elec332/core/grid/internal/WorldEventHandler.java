package elec332.core.grid.internal;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldEventListener;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 23-7-2016.
 */
@SuppressWarnings("all")
public enum WorldEventHandler implements IWorldEventListener {

    INSTANCE;

    @Override
    public void notifyBlockUpdate(IBlockReader worldIn, BlockPos pos, BlockState oldState, BlockState newState, int flags) {
        if (!(worldIn instanceof IWorld)) {
            throw new RuntimeException();
        }
        GridEventInputHandler.INSTANCE.worldBlockUpdate((IWorld) worldIn, pos, oldState, newState);
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable PlayerEntity player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
    }

    @Override
    public void playEvent(PlayerEntity player, int type, BlockPos blockPosIn, int data) {
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
    }

    @Override
    public void addParticle(IParticleData particleData, boolean b, boolean b1, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {

    }

    @Override
    public void addParticle(IParticleData particleData, boolean b, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed) {

    }

}
