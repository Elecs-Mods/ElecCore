package elec332.core.loader;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.world.*;
import elec332.core.util.FMLHelper;
import elec332.core.util.RegistryHelper;
import elec332.core.world.WorldHelper;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.server.ServerChunkProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

/**
 * Created by Elec332 on 17-10-2016.
 * <p>
 * Giant clusterfuck, beware your eyes!
 */
@SuppressWarnings("unused")
@StaticLoad
enum WorldGenManager implements IWorldGenManager {

    INSTANCE;

    WorldGenManager() {
        this.absentGen = input -> HashMultimap.create();
        this.retroGenChunks = Maps.newHashMap();
        this.biomeGen = Maps.newHashMap();

        this.structures_ = Collections.unmodifiableMap(Feature.STRUCTURES);
        this.worldGenRegisters = HashMultimap.create();
        this.chunkHooks = Sets.newHashSet();
        this.hooks = Lists.newArrayList();
        this.features = Sets.newHashSet();
    }

    private final Set<IChunkIOHook> chunkHooks;
    private final Map<Biome, IBiomeGenWrapper> biomeGen;
    private final Multimap<ModContainer, IWorldGenRegister> worldGenRegisters;
    private final List<IWorldEventHook> hooks;

    private final Function<DimensionType, SetMultimap<ChunkPos, ConfiguredFeature<?, ?>>> absentGen;
    private final Map<DimensionType, SetMultimap<ChunkPos, ConfiguredFeature<?, ?>>> retroGenChunks;

    private final Set<Feature<?>> features;
    private final Map<String, Structure<?>> structures_;

    private static final String KEY = "eleccore:worldgenmanager";
    private static final String RETROGEN = "retrogen:keys";

    private ModContainer loadingMC = null;

    @Override
    public void registerBlockChangedHook(IWorldEventHook listener) {
        this.hooks.add(Preconditions.checkNotNull(listener));
    }

    void markBlockChanged(IWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        hooks.forEach(hook -> hook.markBlockChanged(world, pos, oldState, newState));
    }

    @Override
    public void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, Object owner) {
        registerWorldGenRegistry(worldGenRegistry, FMLHelper.getModList().getModContainerByObject(owner).orElseThrow(NullPointerException::new));
    }

    @Override
    public void registerWorldGenRegistry(IWorldGenRegister worldGenRegistry, ModContainer owner) {
        worldGenRegisters.put(owner, worldGenRegistry);
    }

    @Override
    public IBiomeGenWrapper getBiomeRegister(Biome biome) {
        return biomeGen.computeIfAbsent(biome, BiomeRegisterWrapper::new);
    }

    @Override
    public Map<String, Structure<?>> getRegisteredStructures() {
        return structures_;
    }

    void init() {
        worldGenRegisters.forEach((c, r) -> FMLHelper.getFMLModContainer(c).getEventBus().register(new Object() {

            @SubscribeEvent
            public void registerPre(RegistryEvent.Register<Item> event) {
                r.init();
            }

            @SubscribeEvent
            public void registerStuff(RegistryEvent.Register<Feature<?>> event) {
                r.registerFeatures(event.getRegistry());
            }

        }));
    }

    void afterInit() {
        RegistryHelper.getBiomeRegistry().forEach(biome ->
                worldGenRegisters.forEach((mc, worldGenRegister) -> {
                    loadingMC = mc;
                    worldGenRegister.configureBiome(biome, getBiomeRegister(biome));
                    loadingMC = null;
                })
        );
    }

    void afterModsLoaded() {
        for (Feature<?> f : features) {
            ResourceLocation name = f.getRegistryName();
            if (name == null) {
                ElecCore.logger.warn("Detected structure with null name, type: " + f.getClass().getCanonicalName() + " instance: " + f.toString());
            }
            if (!RegistryHelper.getFeatures().containsValue(f)) {
                ElecCore.logger.warn("Detected unregistered structure, type: " + f.getClass().getCanonicalName() + " name: " + f.getRegistryName());
            }
        }
    }

    @SubscribeEvent
    public void chunkLoadFromDisk(ChunkDataEvent.Load event) {
        IChunk chunk = event.getChunk();
        CompoundNBT tag = event.getData().getCompound(KEY);
        for (IChunkIOHook wgh : chunkHooks) {
            wgh.chunkLoadedFromDisk(chunk, tag, this);
        }
        CompoundNBT genTag = tag.getCompound(RETROGEN);
        if (chunk.getStatus().ordinal() > ChunkStatus.FEATURES.ordinal()) {
            BlockPos pos = chunk.getPos().asBlockPos();
            Biome biome = Preconditions.checkNotNull(chunk.getBiomes()).getNoiseBiome(pos.getX(), pos.getY(), pos.getZ());
            Arrays.stream(GenerationStage.Decoration.values())
                    .map(biome::getFeatures)
                    .flatMap(List::stream)
                    .map(this::getRetrogenData)
                    .filter(Objects::nonNull)
                    .forEach(feature -> {
                        IRetroGenFeature<IFeatureConfig> rf = feature.getLeft();
                        IFeatureConfig config = feature.getMiddle();
                        String name = Preconditions.checkNotNull(rf.getName(config));
                        boolean b = genTag.contains(name);
                        if ((!b || !rf.getGenKey(config).equals(genTag.getString(name))) && rf.shouldRetroGen(b, config)) {
                            INSTANCE.registerForRetroGen(Preconditions.checkNotNull(chunk.getWorldForge()), chunk.getPos(), feature.getRight());
                        }
                    });
        }
    }

    private Triple<IRetroGenFeature<IFeatureConfig>, IFeatureConfig, ConfiguredFeature<?, ?>> getRetrogenData(ConfiguredFeature<?, ?> configuredFeature) {
        return getRetrogenData(configuredFeature, configuredFeature);
    }

    private Triple<IRetroGenFeature<IFeatureConfig>, IFeatureConfig, ConfiguredFeature<?, ?>> getRetrogenData(ConfiguredFeature<?, ?> configuredFeature, ConfiguredFeature<?, ?> root) {
        Feature<?> feature = WorldHelper.getFeature(configuredFeature);
        if (feature instanceof IRetroGenFeature) {
            @SuppressWarnings("unchecked")
            IRetroGenFeature<IFeatureConfig> structure = (IRetroGenFeature<IFeatureConfig>) feature;
            IFeatureConfig config = WorldHelper.getFeatureConfiguration(configuredFeature);
            return Triple.of(structure, config, root);
        } else if (WorldHelper.getFeatureConfiguration(configuredFeature) instanceof DecoratedFeatureConfig) {
            return getRetrogenData(((DecoratedFeatureConfig) WorldHelper.getFeatureConfiguration(configuredFeature)).feature, root);
        }
        return null;
    }

    @SubscribeEvent
    public void chunkSaveToDisk(ChunkDataEvent.Save event) {
        IChunk chunk = event.getChunk();
        CompoundNBT tag = new CompoundNBT();
        for (IChunkIOHook wgh : chunkHooks) {
            wgh.chunkSavedToDisk(chunk, tag, this);
        }
        if (chunk.getStatus().ordinal() > ChunkStatus.FEATURES.ordinal()) {
            CompoundNBT genTag = new CompoundNBT();
            BlockPos pos = chunk.getPos().asBlockPos();
            Biome biome = Preconditions.checkNotNull(chunk.getBiomes()).getNoiseBiome(pos.getX(), pos.getY(), pos.getZ());
            Arrays.stream(GenerationStage.Decoration.values())
                    .map(biome::getFeatures)
                    .flatMap(List::stream)
                    .map(this::getRetrogenData)
                    .filter(Objects::nonNull)
                    .forEach(feature -> {
                        IRetroGenFeature<IFeatureConfig> rf = feature.getLeft();
                        IFeatureConfig config = feature.getMiddle();
                        String name = Preconditions.checkNotNull(rf.getName(config));
                        genTag.putString(name, rf.getGenKey(config));
                    });
            tag.put(RETROGEN, genTag);
        }
        event.getData().put(KEY, tag);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        SetMultimap<ChunkPos, ConfiguredFeature<?, ?>> map = retroGenChunks.computeIfAbsent(WorldHelper.getDimID(event.world), absentGen);
        if (!map.isEmpty()) {
            World world = event.world;
            ChunkPos pos = map.keySet().iterator().next();
            if (WorldHelper.chunkLoaded(world, pos.asBlockPos())) {
                Set<ConfiguredFeature<?, ?>> s = map.removeAll(pos);
                if (s != null && !s.isEmpty()) {
                    //System.out.println("Retrogen @ " + pos + " for " + s.stream().map(WorldHelper::getFeature).collect(Collectors.toList()));

                    int x = pos.x;
                    int z = pos.z;
                    for (int i = -2; i < 3; i++) {
                        for (int j = -2; j < 3; j++) {
                            if (world.chunkExists(x + i, z + j)) {
                                Chunk c = world.getChunk(x + i, z + j);
                                Map<Heightmap.Type, Heightmap> heightMap = c.heightMap;
                                if (!heightMap.containsKey(Heightmap.Type.OCEAN_FLOOR_WG)) {
                                    heightMap.put(Heightmap.Type.OCEAN_FLOOR_WG, heightMap.get(Heightmap.Type.OCEAN_FLOOR));
                                }
                                if (!heightMap.containsKey(Heightmap.Type.WORLD_SURFACE_WG)) {
                                    heightMap.put(Heightmap.Type.WORLD_SURFACE_WG, heightMap.get(Heightmap.Type.WORLD_SURFACE));
                                }
                            }
                        }
                    }

                    long worldSeed = world.getSeed();
                    Random random = new Random(worldSeed);
                    long xSeed = random.nextLong() >> 2 + 1L;
                    long zSeed = random.nextLong() >> 2 + 1L;
                    long seed = xSeed * pos.x + zSeed * pos.z ^ worldSeed;
                    for (ConfiguredFeature<?, ?> featureGenerator : s) {
                        random.setSeed(seed);
                        AbstractChunkProvider chunkProvider = world.getChunkProvider();
                        if (chunkProvider instanceof ServerChunkProvider) {
                            featureGenerator.place(world, ((ServerChunkProvider) chunkProvider).getChunkGenerator(), random, pos.asBlockPos());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, ConfiguredFeature<?, ?>... featureGenerators) {
        if (featureGenerators == null || featureGenerators.length == 0) {
            return;
        }
        registerForRetroGen(world, chunk, Lists.newArrayList(featureGenerators));
    }

    @Override
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, Collection<ConfiguredFeature<?, ?>> featureGenerators) {
        if (featureGenerators == null || featureGenerators.size() == 0) {
            return;
        }
        retroGenChunks.computeIfAbsent(WorldHelper.getDimID(world), absentGen).putAll(chunk, featureGenerators);
    }

    @Override
    public boolean register(IChunkIOHook chunkIOHook) {
        return FMLHelper.isInModInitialisation() && chunkHooks.add(chunkIOHook);
    }

    @APIHandlerInject
    public void injectWorldGenManager(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, IWorldGenManager.class);
    }

    private class BiomeRegisterWrapper implements IBiomeGenWrapper {

        private BiomeRegisterWrapper(Biome biome) {
            this.biome = biome;
        }

        private final Biome biome;

        @Override
        public Biome getBiome() {
            return biome;
        }

        @Override
        public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<PC> placement, PC pc) {
            addFeature(decorationStage, feature.withConfiguration(fc), placement.configure(pc));
        }

        @Override
        public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<FC, ? extends Feature<FC>> configuredFeature, ConfiguredPlacement<PC> placement) {
            features.add(WorldHelper.getFeature(configuredFeature));
            addFeature(decorationStage, configuredFeature.withPlacement(placement));
        }

        @Override
        public <C extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<C, ? extends Feature<C>> feature) {
            ConfiguredFeature<IFeatureConfig, ? extends Structure<IFeatureConfig>> structure = getStructure(feature);
            if (structure != null) {
                biome.addStructure(structure);
            }
            biome.addFeature(decorationStage, feature);
        }

        @SuppressWarnings("unchecked")
        private ConfiguredFeature<IFeatureConfig, ? extends Structure<IFeatureConfig>> getStructure(ConfiguredFeature<?, ?> configuredFeature) {
            features.add(WorldHelper.getFeature(configuredFeature));
            if (WorldHelper.getFeature(configuredFeature) instanceof Structure) {
                Structure<IFeatureConfig> structure = ((Structure<IFeatureConfig>) WorldHelper.getFeature(configuredFeature));
                IFeatureConfig config = getBiome().getStructureConfig(structure);
                if (config == null) {
                    config = WorldHelper.getFeatureConfiguration(configuredFeature);
                }
                return structure.withConfiguration(config);
            } else if (WorldHelper.getFeatureConfiguration(configuredFeature) instanceof DecoratedFeatureConfig) {
                return getStructure(((DecoratedFeatureConfig) WorldHelper.getFeatureConfiguration(configuredFeature)).feature);
            }
            return null;
        }

        @Override
        public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config) {
            features.add(structure);
            getBiome().addStructure(new ConfiguredFeature<>(structure, config));
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig) {
            if (!RegistryHelper.getCarvers().containsValue(carver)) {
                ElecCore.logger.warn("Detected unregistered world carver, type: " + carver.getClass().getCanonicalName() + " name: " + carver.getRegistryName());
            }
            addCarver(stage, new ConfiguredCarver<>(carver, carverConfig));
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, ConfiguredCarver<C> carver) {
            if (!RegistryHelper.getCarvers().containsValue(carver.carver)) {
                ElecCore.logger.warn("Detected unregistered world carver, type: " + carver.getClass().getCanonicalName() + " name: " + carver.carver.getRegistryName());
            }
            getBiome().addCarver(stage, carver);
        }

        @Override
        public void addSpawn(EntityClassification type, EntityType<? extends LivingEntity> entityType, int weight, int minGroupCount, int maxGroupCount) {
            addSpawn(type, new Biome.SpawnListEntry(entityType, weight, maxGroupCount, maxGroupCount));
        }

        @Override
        public void addSpawn(EntityClassification type, Biome.SpawnListEntry spawnListEntry) {
            WorldHelper.addBiomeSpawnEntry(getBiome(), type, spawnListEntry);
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

}
