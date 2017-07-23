package elec332.core.mcabstractionlayer.impl;

import com.google.common.collect.Sets;
import elec332.core.world.IElecWorldEventListener;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Created by Elec332 on 30-1-2017.
 */
@SuppressWarnings("all")
public enum WorldEventListener implements IWorldEventListener {

    INSTANCE;

    public static void unRegisterWorldEventListener(IElecWorldEventListener listener){
        listeners.remove(listener);
    }

    public static void registerWorldEventListener(IElecWorldEventListener listener){
        listeners.add(listener);
    }

    private static Set<IElecWorldEventListener> listeners = Sets.newHashSet();

    @Override
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags) {
        for (IElecWorldEventListener l : listeners) {
            l.notifyBlockUpdate(worldIn, pos, oldState, newState, flags);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos) {
        for (IElecWorldEventListener l : listeners){
            l.notifyLightSet(pos);
        }
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
        for (IElecWorldEventListener l : listeners){
            l.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
        }
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch) {
        for (IElecWorldEventListener l : listeners){
            l.playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
        }
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos) {
        for (IElecWorldEventListener l : listeners){
            l.playRecord(soundIn, pos);
        }
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        spawnParticle(particleID, ignoreRange, false, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, boolean minParticles, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters) {
        for (IElecWorldEventListener l : listeners){
            l.spawnParticle(particleID, ignoreRange, minParticles, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
        }
    }

    @Override
    public void onEntityAdded(Entity entityIn) {
        for (IElecWorldEventListener l : listeners){
            l.onEntityAdded(entityIn);
        }
    }

    @Override
    public void onEntityRemoved(Entity entityIn) {
        for (IElecWorldEventListener l : listeners){
            l.onEntityRemoved(entityIn);
        }
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data) {
        for (IElecWorldEventListener l : listeners){
            l.broadcastSound(soundID, pos, data);
        }
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data) {
        for (IElecWorldEventListener l : listeners){
            l.playEvent(player, type, blockPosIn, data);
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
        for (IElecWorldEventListener l : listeners){
            l.sendBlockBreakProgress(breakerId, pos, progress);
        }
    }

    public static void register(){
    }

    static {
        MinecraftForge.EVENT_BUS.register(new Object(){

            @SubscribeEvent
            public void loadWorld(WorldEvent.Load event){
                World world = event.getWorld();
                if (!world.isRemote){
                    world.removeEventListener(INSTANCE);
                    world.addEventListener(INSTANCE);
                }
            }

        });
    }

}
