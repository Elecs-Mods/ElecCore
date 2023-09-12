package elec332.core.grid.internal;

import com.mojang.datafixers.util.Either;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.concurrent.DelegatedTaskExecutor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.server.ChunkHolder;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraft.world.server.ServerWorldLightManager;
import net.minecraft.world.server.TicketType;
import net.minecraft.world.storage.DimensionSavedDataManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;

/**
 * Created by Elec332 on 22-12-2019
 */
class WrappedServerChunkProvider extends ServerChunkProvider {

    @SuppressWarnings("all")
    public WrappedServerChunkProvider(ServerChunkProvider spr) {
        super(spr.world, new File("noop"), null, null, command -> {
            if (command instanceof DelegatedTaskExecutor) {
                command.run();
            }
        }, spr.generator, -1, null, null);
        this.chunkManager = spr.chunkManager;
        this.scp = spr;
    }

    private final ServerChunkProvider scp;

    @Nonnull
    @Override
    public ServerWorldLightManager getLightManager() {
        return scp.getLightManager();
    }

    @Nullable
    @Override
    public IChunk getChunk(int p_212849_1_, int p_212849_2_, @Nonnull ChunkStatus p_212849_3_, boolean p_212849_4_) {
        return scp.getChunk(p_212849_1_, p_212849_2_, p_212849_3_, p_212849_4_);
    }

    @Override
    public boolean chunkExists(int p_73149_1_, int p_73149_2_) {
        return scp.chunkExists(p_73149_1_, p_73149_2_);
    }

    @Override
    public IBlockReader getChunkForLight(int p_217202_1_, int p_217202_2_) {
        return scp.getChunkForLight(p_217202_1_, p_217202_2_);
    }

    @Nonnull
    @Override
    public World getWorld() {
        return scp.getWorld();
    }

    @Override
    public boolean isChunkLoaded(Entity p_217204_1_) {
        return scp.isChunkLoaded(p_217204_1_);
    }

    @Override
    public boolean isChunkLoaded(ChunkPos p_222865_1_) {
        return scp.isChunkLoaded(p_222865_1_);
    }

    @Override
    public boolean canTick(BlockPos p_222866_1_) {
        return scp.canTick(p_222866_1_);
    }

    @Override
    public void save(boolean p_217210_1_) {
        scp.save(p_217210_1_);
    }

    @Override
    public void close() throws IOException {
        scp.close();
    }

    @Override
    public void tick(@Nonnull BooleanSupplier p_217207_1_) {
        scp.tick(p_217207_1_);
    }

    @Nonnull
    @Override
    public String makeString() {
        return scp.makeString();
    }

    @Override
    @SuppressWarnings("all")
    public ChunkGenerator<?> getChunkGenerator() {
        if (scp == null) {
            return null;
        }
        return scp.getChunkGenerator();
    }

    @Override
    public int getLoadedChunkCount() {
        return scp.getLoadedChunkCount();
    }

    @Override
    public void markBlockChanged(BlockPos p_217217_1_) {
        GridEventInputHandler.INSTANCE.worldBlockUpdate(getWorld(), p_217217_1_, null, null);
        scp.markBlockChanged(p_217217_1_);
    }

    @Override
    public void markLightChanged(@Nonnull LightType p_217201_1_, SectionPos p_217201_2_) {
        scp.markLightChanged(p_217201_1_, p_217201_2_);
    }

    @Override
    public void forceChunk(@Nonnull ChunkPos p_217206_1_, boolean p_217206_2_) {
        scp.forceChunk(p_217206_1_, p_217206_2_);
    }

    @Override
    public void updatePlayerPosition(@Nonnull ServerPlayerEntity p_217221_1_) {
        scp.updatePlayerPosition(p_217221_1_);
    }

    @Override
    public void untrack(@Nonnull Entity p_217226_1_) {
        scp.untrack(p_217226_1_);
    }

    @Override
    public void track(@Nonnull Entity p_217230_1_) {
        scp.track(p_217230_1_);
    }

    @Override
    public void sendToTrackingAndSelf(@Nonnull Entity p_217216_1_, @Nonnull IPacket<?> p_217216_2_) {
        scp.sendToTrackingAndSelf(p_217216_1_, p_217216_2_);
    }

    @Override
    public void sendToAllTracking(@Nonnull Entity p_217218_1_, @Nonnull IPacket<?> p_217218_2_) {
        scp.sendToAllTracking(p_217218_1_, p_217218_2_);
    }

    @Override
    public void setViewDistance(int p_217219_1_) {
        scp.setViewDistance(p_217219_1_);
    }

    @Override
    public void setAllowedSpawnTypes(boolean p_217203_1_, boolean p_217203_2_) {
        scp.setAllowedSpawnTypes(p_217203_1_, p_217203_2_);
    }

    @Nonnull
    @Override
    public DimensionSavedDataManager getSavedData() {
        return scp.getSavedData();
    }

    @Nonnull
    @Override
    public PointOfInterestManager getPointOfInterestManager() {
        return scp.getPointOfInterestManager();
    }

    @Nonnull
    @Override
    public CompletableFuture<Either<IChunk, ChunkHolder.IChunkLoadingError>> func_217232_b(int p_217232_1_, int p_217232_2_, ChunkStatus p_217232_3_, boolean p_217232_4_) {
        return scp.func_217232_b(p_217232_1_, p_217232_2_, p_217232_3_, p_217232_4_);
    }

    @Nullable
    @Override
    public Chunk getChunk(int p_217205_1_, int p_217205_2_, boolean p_217205_3_) {
        return scp.getChunk(p_217205_1_, p_217205_2_, p_217205_3_);
    }

    @Override
    public boolean func_217234_d() {
        return scp.func_217234_d();
    }

    @Override
    public boolean func_223435_b(Entity p_223435_1_) {
        return scp.func_223435_b(p_223435_1_);
    }

    @Nullable
    @Override
    public Chunk func_225313_a(int p_225313_1_, int p_225313_2_) {
        return scp.func_225313_a(p_225313_1_, p_225313_2_);
    }

    @Override
    public int func_217229_b() {
        return scp.func_217229_b();
    }

    @Override
    public int func_225314_f() {
        return scp.func_225314_f();
    }

    @Nonnull
    @Override
    public String func_217208_a(@Nonnull ChunkPos p_217208_1_) {
        return scp.func_217208_a(p_217208_1_);
    }

    @Override
    public <T> void func_217222_b(@Nonnull TicketType<T> p_217222_1_, @Nonnull ChunkPos p_217222_2_, int p_217222_3_, @Nonnull T p_217222_4_) {
        scp.func_217222_b(p_217222_1_, p_217222_2_, p_217222_3_, p_217222_4_);
    }

    @Override
    public <T> void func_217228_a(@Nonnull TicketType<T> p_217228_1_, @Nonnull ChunkPos p_217228_2_, int p_217228_3_, @Nonnull T p_217228_4_) {
        scp.func_217228_a(p_217228_1_, p_217228_2_, p_217228_3_, p_217228_4_);
    }

}
