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
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
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
import net.minecraft.world.server.ServerWorld;
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
import java.util.function.Supplier;

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

        this.worldGenRegisters = HashMultimap.create();
        this.chunkHooks = Sets.newHashSet();
        this.hooks = Lists.newArrayList();
        this.features = Sets.newHashSet();
        this.structures = Sets.newHashSet();
    }

    private final Set<IChunkIOHook> chunkHooks;
    private final Map<Biome, IBiomeGenWrapper> biomeGen;
    private final Multimap<ModContainer, IWorldGenRegister> worldGenRegisters;
    private final List<IWorldEventHook> hooks;

    private final Function<RegistryKey<World>, SetMultimap<ChunkPos, ConfiguredFeature<?, ?>>> absentGen;
    private final Map<RegistryKey<World>, SetMultimap<ChunkPos, ConfiguredFeature<?, ?>>> retroGenChunks;

    private final Set<Feature<?>> features;
    private final Set<Structure<?>> structures;

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
    public Set<Map.Entry<ResourceLocation, Structure<?>>> getRegisteredStructures() {
        return RegistryHelper.getStructures().getEntries();
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
        WorldGenRegistries.field_243657_i.forEach(biome ->
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
                ElecCore.logger.warn("Detected feature with null name, type: " + f.getClass().getCanonicalName() + " instance: " + f.toString());
            }
            if (!RegistryHelper.getFeatures().containsValue(f)) {
                ElecCore.logger.warn("Detected unregistered feature, type: " + f.getClass().getCanonicalName() + " name: " + f.getRegistryName());
            }
        }
        for (Structure<?> f : structures) {
            ResourceLocation name = f.getRegistryName();
            if (name == null) {
                ElecCore.logger.warn("Detected structure with null name, type: " + f.getClass().getCanonicalName() + " instance: " + f.toString());
            }
            if (!RegistryHelper.getStructures().containsValue(f)) {
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
            biome.func_242440_e().field_242484_f.stream()
                    .flatMap(List::stream)
                    .map(Supplier::get)
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
            return getRetrogenData(((DecoratedFeatureConfig) WorldHelper.getFeatureConfiguration(configuredFeature)).feature.get(), root);
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

            biome.func_242440_e().field_242484_f.stream()
                    .flatMap(List::stream)
                    .map(Supplier::get)
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
            if (WorldHelper.isClient(event.world)) {
                throw new RuntimeException();
            }
            ServerWorld world = (ServerWorld) event.world;
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
                        featureGenerator.func_242765_a(world, world.getChunkProvider().getChunkGenerator(), random, pos.asBlockPos());
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
            BiomeGenerationSettings settings = biome.func_242440_e();

            Map<GenerationStage.Carving, List<Supplier<ConfiguredCarver<?>>>> carvers = settings.field_242483_e;
            settings.field_242483_e = Maps.newHashMap();
            carvers.forEach((c, l) -> settings.field_242483_e.put(c, Lists.newArrayList(l)));

            List<List<Supplier<ConfiguredFeature<?, ?>>>> features = settings.field_242484_f;
            settings.field_242484_f = Lists.newArrayList();
            features.forEach(l -> settings.field_242484_f.add(Lists.newArrayList(l)));

            settings.field_242485_g = Lists.newArrayList(settings.field_242485_g);

            settings.field_242486_h = Lists.newArrayList(settings.field_242486_h);
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
            addFeature(decorationStage, configuredFeature.withPlacement(placement));
        }

        @Override
        public <C extends IFeatureConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<C, ? extends Feature<C>> feature) {
            List<List<Supplier<ConfiguredFeature<?, ?>>>> features = biome.func_242440_e().field_242484_f;
            while (features.size() <= decorationStage.ordinal()) {
                features.add(Lists.newArrayList());
            }
            WorldGenManager.this.features.add(WorldHelper.getFeature(feature));
            features.get(decorationStage.ordinal()).add(() -> feature);
            feature.func_242768_d().filter(f -> f.feature == Feature.FLOWER).forEach(biome.func_242440_e().field_242486_h::add);
        }

        @Override
        public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config) {
            addStructure(new StructureFeature<>(structure, config));
        }

        @Override
        public <C extends IFeatureConfig> void addStructure(StructureFeature<C, Structure<C>> configuredStructure) {
            structures.add(configuredStructure.field_236268_b_);
            getBiome().func_242440_e().field_242485_g.add(() -> configuredStructure);
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig) {
            addCarver(stage, new ConfiguredCarver<>(carver, carverConfig));
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, ConfiguredCarver<C> carver) {
            if (!RegistryHelper.getCarvers().containsValue(carver.carver)) {
                ElecCore.logger.warn("Detected unregistered world carver, type: " + carver.getClass().getCanonicalName() + " name: " + carver.carver.getRegistryName());
            }
            getBiome().func_242440_e().field_242483_e.computeIfAbsent(stage, s -> Lists.newArrayList()).add(() -> carver);
        }

        @Override
        public void addSpawn(EntityClassification type, EntityType<? extends LivingEntity> entityType, int weight, int minGroupCount, int maxGroupCount) {
            addSpawn(type, new MobSpawnInfo.Spawners(entityType, weight, maxGroupCount, maxGroupCount));
        }

        @Override
        public void addSpawn(EntityClassification type, MobSpawnInfo.Spawners spawnListEntry) {
            WorldHelper.addBiomeSpawnEntry(getBiome(), type, spawnListEntry);
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

}
