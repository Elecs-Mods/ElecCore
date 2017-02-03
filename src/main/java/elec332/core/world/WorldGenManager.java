package elec332.core.world;

import com.google.common.base.Strings;
import com.google.common.collect.*;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.world.*;
import elec332.core.java.MapWithDefaultValue;
import elec332.core.main.APIHandler;
import elec332.core.util.FMLUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 17-10-2016.
 */
@SuppressWarnings("unused")
@APIHandler.StaticLoad
enum WorldGenManager implements ISingleObjectRegistry<IWorldGenHook>, IWorldGenManager {

    INSTANCE;

    WorldGenManager(){
        this.set = Sets.newHashSet();
        this.set_ = Collections.unmodifiableSet(set);
        this.retroGenChunks = MapWithDefaultValue.newMap(Maps.newHashMap(), new Callable<SetMultimap<ChunkPos, IFeatureGenerator>>() {

            @Override
            public SetMultimap<ChunkPos, IFeatureGenerator> call() throws Exception {
                return HashMultimap.create();
            }

        });
    }

    private final Set<IWorldGenHook> set, set_;
    private final Map<Integer, SetMultimap<ChunkPos, IFeatureGenerator>> retroGenChunks;
    private static final String KEY = "ElecWorldGenManager";

    @SubscribeEvent
    public void populateChunk(PopulateChunkEvent.Post event) {
        for (IWorldGenHook wgh : set_){
            wgh.populateChunk(event.getGenerator(), event.getWorld(), event.getRand(), event.getChunkX(), event.getChunkZ(), event.isHasVillageGenerated());
        }
    }

    @SubscribeEvent
    public void chunkLoadFromDisk(ChunkDataEvent.Load event){
        Chunk chunk = event.getChunk();
        NBTTagCompound tag = event.getData().getCompoundTag(KEY);
        for (IWorldGenHook wgh : set_){
            wgh.chunkLoadedFromDisk(chunk, tag, this);
        }
    }

    @SubscribeEvent
    public void chunkSaveToDisk(ChunkDataEvent.Save event){
        Chunk chunk = event.getChunk();
        NBTTagCompound tag = new NBTTagCompound();
        for (IWorldGenHook wgh : set_){
            wgh.chunkSavedToDisk(chunk, tag, this);
        }
        event.getData().setTag(KEY, tag);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event){
        SetMultimap<ChunkPos, IFeatureGenerator> map = retroGenChunks.get(WorldHelper.getDimID(event.world));
        if (!map.isEmpty()){
            ChunkPos pos = map.keySet().iterator().next();
            Set<IFeatureGenerator> s = map.removeAll(pos);
            if (s != null && !s.isEmpty()) {
                long worldSeed = event.world.getSeed();
                Random random = new Random(worldSeed);
                long xSeed = random.nextLong() >> 2 + 1L;
                long zSeed = random.nextLong() >> 2 + 1L;
                long seed = xSeed * pos.chunkXPos + zSeed * pos.chunkZPos ^ worldSeed;
                for (IFeatureGenerator featureGenerator : s) {
                    random.setSeed(seed);
                    featureGenerator.generateFeature(random, pos.chunkXPos, pos.chunkZPos, event.world);
                }
            }
        }
    }

    private class ChunkPopulatorWrapper implements IWorldGenHook, IFeatureGenerator {

        private ChunkPopulatorWrapper(IAdvancedChunkPopulator chunkPopulator, String owner){
            this.chunkPopulator = chunkPopulator;
            this.owner = owner.toLowerCase();
        }

        private final IAdvancedChunkPopulator chunkPopulator;
        private final String owner;
        private String lastKey;

        @Override
        public String getName() {
            return chunkPopulator.getName();
        }

        @Override
        public void populateChunk(IChunkGenerator chunkGenerator, World world, Random rand, int chunkX, int chunkZ, boolean hasVillageGenerated){
            chunkPopulator.populateChunk(chunkGenerator, world, rand, chunkX, chunkZ, hasVillageGenerated);
            this.lastKey = chunkPopulator.getGenKey();
        }

        @Override
        public void chunkLoadedFromDisk(Chunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager) {
            NBTTagCompound tag = data.getCompoundTag(owner);
            boolean b = tag.hasKey(chunkPopulator.getName());
            if ((!b || !chunkPopulator.getGenKey().equals(tag.getString(chunkPopulator.getName()))) && chunkPopulator.shouldRegen(b)){
                worldGenManager.registerForRetroGen(chunk.getWorld(), chunk.getChunkCoordIntPair(), this);
            }  else {
                this.lastKey = chunkPopulator.getGenKey();
            }
        }

        @Override
        public void chunkSavedToDisk(Chunk chunk, NBTTagCompound data, IWorldGenManager worldGenManager) {
            NBTTagCompound tag = data.getCompoundTag(owner);
            if (!Strings.isNullOrEmpty(this.lastKey)) {
                tag.setString(chunkPopulator.getName(), this.lastKey);
            }
            data.setTag(owner, tag);
        }

        @Override
        public boolean generateFeature(Random random, int chunkX, int chunkZ, World world) {
            boolean ret = chunkPopulator.retroGen(random, chunkX, chunkZ, world);
            this.lastKey = chunkPopulator.getGenKey();
            return ret;
        }

    }

    @Override
    public boolean register(IAdvancedChunkPopulator chunkPopulator) {
        if (!FMLUtil.isInModInitialisation()){
            return false;
        }
        ModContainer mc = FMLUtil.getLoader().activeModContainer();
        if (mc == null){
            throw new IllegalArgumentException();
        }
        return register((IWorldGenHook)new ChunkPopulatorWrapper(chunkPopulator, mc.getModId()));
    }

    @Override
    public boolean register(IFeatureGenerator featureGenerator) {
        return FMLUtil.isInModInitialisation() && register(new FeatureGeneratorWrapper(featureGenerator));
    }

    @Override
    public void registerForRetroGen(@Nonnull World world, @Nonnull ChunkPos chunk, IFeatureGenerator... featureGenerators) {
        if (featureGenerators == null || featureGenerators.length == 0){
            return;
        }
        registerForRetroGen(world, chunk, Lists.newArrayList());
    }

    @Override
    public void registerForRetroGen(@Nonnull World world, @Nonnull ChunkPos chunk, Collection<IFeatureGenerator> featureGenerators) {
        if (featureGenerators == null || featureGenerators.size() == 0){
            return;
        }
        retroGenChunks.get(WorldHelper.getDimID(world)).putAll(chunk, featureGenerators);
    }

    @Override
    public boolean register(IWorldGenHook wgh) {
        return FMLUtil.isInModInitialisation() && set.add(wgh);
    }

    @Override
    public Set<IWorldGenHook> getAllRegisteredObjects() {
        return set_;
    }

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
        APIHandler.INSTANCE.inject(INSTANCE, IWorldGenManager.class);
    }

}
