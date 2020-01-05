package elec332.core.loader;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.*;
import elec332.core.ElecCore;
import elec332.core.api.APIHandlerInject;
import elec332.core.api.IAPIHandler;
import elec332.core.api.annotations.StaticLoad;
import elec332.core.api.registration.IWorldGenRegister;
import elec332.core.api.registry.ISingleObjectRegistry;
import elec332.core.api.world.*;
import elec332.core.util.FMLHelper;
import elec332.core.util.RegistryHelper;
import elec332.core.world.FeaturePlacers;
import elec332.core.world.WorldHelper;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.carver.ICarverConfig;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Elec332 on 17-10-2016.
 * <p>
 * Giant clusterfuck, beware your eyes!
 */
@SuppressWarnings("unused")
@StaticLoad
enum WorldGenManager implements ISingleObjectRegistry<IWorldGenHook>, IWorldGenManager {

    INSTANCE;

    WorldGenManager() {
        this.set = Sets.newHashSet();
        this.set_ = Collections.unmodifiableSet(set);
        this.absentGen = input -> HashMultimap.create();
        this.retroGenChunks = Maps.newHashMap();
        this.biomeGen = Maps.newHashMap();
        this.structurez = Sets.newHashSet();
        this.registeredFeatures = Sets.newHashSet();
        this.structures = Feature.STRUCTURES;
        this.structures_ = Collections.unmodifiableMap(structures);
        this.worldGenRegisters = HashMultimap.create();
        this.chunkHooks = Sets.newHashSet();
        this.retroGennableNames = Sets.newHashSet();
    }

    private final Function<DimensionType, SetMultimap<ChunkPos, ILegacyFeatureGenerator>> absentGen;
    private final Set<IWorldGenHook> set, set_;
    private final Set<IChunkIOHook> chunkHooks;
    private final Map<DimensionType, SetMultimap<ChunkPos, ILegacyFeatureGenerator>> retroGenChunks;
    private final Map<Biome, IBiomeGenWrapper> biomeGen;
    private final Set<Structure<?>> structurez;
    private final Set<Triple<Structure<?>, ? extends IFeatureConfig, String>> registeredFeatures;
    private final Map<String, Structure<?>> structures, structures_;
    private final Multimap<ModContainer, IWorldGenRegister> worldGenRegisters;
    private final Set<String> retroGennableNames;
    private static final String KEY = "eleccore:worldgenmanager";

    private ModContainer loadingMC = null;

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

    void afterInit() {
        RegistryHelper.getBiomeRegistry().forEach(biome ->
                worldGenRegisters.forEach((mc, worldGenRegister) -> {
                    loadingMC = mc;
                    worldGenRegister.registerFeatures(biome, getBiomeRegister(biome));
                    loadingMC = null;
                })
        );
    }

    @SuppressWarnings(" unchecked")
    void afterModsLoaded() {
        structurez.removeIf(
                structure -> StreamSupport.stream(RegistryHelper.getBiomeRegistry().spliterator(), false)
                        .anyMatch(biome -> biome.hasStructure(structure))
        );
        if (!structurez.isEmpty()) {
            structurez.forEach(f -> ElecCore.logger.warn("Detected unregistered structure, type: " + f.getClass().getCanonicalName() + " instance: " + f.toString()));
        }

        Set<Structure<?>> featStruc = //All structures registered as features in all biomes
                StreamSupport.stream(RegistryHelper.getBiomeRegistry().spliterator(), false)
                        .flatMap(biome ->
                                Arrays.stream(GenerationStage.Decoration.values())
                                        .map(biome::getFeatures)
                                        .flatMap(Collection::stream))
                        .map(WorldHelper::getFeature)
                        .filter(f -> f instanceof Structure)
                        .map(f -> (Structure<?>) f)
                        .collect(Collectors.toSet());

        registeredFeatures.forEach(t -> {
            if (!featStruc.contains(t.getLeft())) {
                ElecCore.logger.warn("Detected structure not registered as feature: " + t.getRight());
                ElecCore.logger.info("Registering structure feature " + t.getRight() + " as surface structure to all biomes...");
                StreamSupport.stream(RegistryHelper.getBiomeRegistry().spliterator(), false)
                        .map(WorldGenManager.INSTANCE::getBiomeRegister)
                        .forEach(reg -> reg.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, (Feature<IFeatureConfig>) t.getLeft(), t.getMiddle(), FeaturePlacers.PASSTHROUGH));
            }
        });
    }

    boolean legacyPopulateChunk(IWorld world, ChunkGenerator<? extends GenerationSettings> chunkGenerator, Random random, BlockPos chunkXYWorld) {
        ChunkPos pos = WorldHelper.chunkPosFromBlockPos(chunkXYWorld);
        boolean b = false;
        for (IWorldGenHook wgh : set_) {
            b |= wgh.populateChunk(chunkGenerator, world, random, pos.x, pos.z);
        }
        return b;
    }

    @SubscribeEvent
    public void chunkLoadFromDisk(ChunkDataEvent.Load event) {
        IChunk chunk = event.getChunk();
        CompoundNBT tag = event.getData().getCompound(KEY);
        for (IChunkIOHook wgh : chunkHooks) {
            wgh.chunkLoadedFromDisk(chunk, tag, this);
        }
    }

    @SubscribeEvent
    public void chunkSaveToDisk(ChunkDataEvent.Save event) {
        IChunk chunk = event.getChunk();
        CompoundNBT tag = new CompoundNBT();
        for (IChunkIOHook wgh : chunkHooks) {
            wgh.chunkSavedToDisk(chunk, tag, this);
        }
        event.getData().put(KEY, tag);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        SetMultimap<ChunkPos, ILegacyFeatureGenerator> map = retroGenChunks.computeIfAbsent(WorldHelper.getDimID(event.world), absentGen);
        if (!map.isEmpty()) {
            ChunkPos pos = map.keySet().iterator().next();
            Set<ILegacyFeatureGenerator> s = map.removeAll(pos);
            if (s != null && !s.isEmpty()) {
                World world = event.world;
                long worldSeed = world.getSeed();
                Random random = new Random(worldSeed);
                long xSeed = random.nextLong() >> 2 + 1L;
                long zSeed = random.nextLong() >> 2 + 1L;
                long seed = xSeed * pos.x + zSeed * pos.z ^ worldSeed;
                for (ILegacyFeatureGenerator featureGenerator : s) {
                    random.setSeed(seed);
                    featureGenerator.generateFeature(world, pos.x, pos.z, random, true);
                }
            }
        }
    }

    @Override
    public boolean register(IAdvancedChunkPopulator chunkPopulator) {
        if (!FMLHelper.isInModInitialisation()) {
            return false;
        }
        ModContainer mc = FMLHelper.getActiveModContainer();
        if (mc == null) {
            throw new IllegalArgumentException();
        }
        return register((IWorldGenHook) new ChunkPopulatorWrapper(chunkPopulator, mc.getModId()));
    }

    @Override
    public boolean register(ILegacyFeatureGenerator featureGenerator) {
        return FMLHelper.isInModInitialisation() && register(new FeatureGeneratorWrapper(featureGenerator));
    }

    @Override
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, ILegacyFeatureGenerator... featureGenerators) {
        if (featureGenerators == null || featureGenerators.length == 0) {
            return;
        }
        registerForRetroGen(world, chunk, Lists.newArrayList(featureGenerators));
    }

    @Override
    public void registerForRetroGen(@Nonnull IWorld world, @Nonnull ChunkPos chunk, Collection<ILegacyFeatureGenerator> featureGenerators) {
        if (featureGenerators == null || featureGenerators.size() == 0) {
            return;
        }
        retroGenChunks.computeIfAbsent(WorldHelper.getDimID(world), absentGen).putAll(chunk, featureGenerators);
    }

    @Override
    public boolean register(IWorldGenHook wgh) {
        return FMLHelper.isInModInitialisation() && set.add(wgh) && register((IChunkIOHook) wgh);
    }

    @Override
    public boolean register(IChunkIOHook chunkIOHook) {
        return FMLHelper.isInModInitialisation() && chunkHooks.add(chunkIOHook);
    }

    @Override
    public Set<IWorldGenHook> getAllRegisteredObjects() {
        return set_;
    }

    @APIHandlerInject
    public void injectWorldGenManager(IAPIHandler apiHandler) {
        apiHandler.inject(INSTANCE, IWorldGenManager.class);
    }

    private static class ChunkPopulatorWrapper implements IWorldGenHook, ILegacyFeatureGenerator {

        private ChunkPopulatorWrapper(IAdvancedChunkPopulator chunkPopulator, String owner) {
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
        public boolean populateChunk(ChunkGenerator chunkGenerator, IWorld world, Random rand, int chunkX, int chunkZ) {
            boolean b = chunkPopulator.populateChunk(chunkGenerator, world, rand, chunkX, chunkZ);
            this.lastKey = chunkPopulator.getGenKey();
            return b;
        }

        @Override
        public void chunkLoadedFromDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager) {
            CompoundNBT tag = data.getCompound(owner);
            boolean b = tag.contains(chunkPopulator.getName());
            if ((!b || !chunkPopulator.getGenKey().equals(tag.getString(chunkPopulator.getName()))) && chunkPopulator.shouldRegen(b)) {
                worldGenManager.registerForRetroGen(Preconditions.checkNotNull(chunk.getWorldForge()), chunk.getPos(), this);
            } else {
                this.lastKey = chunkPopulator.getGenKey();
            }
        }

        @Override
        public void chunkSavedToDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager) {
            CompoundNBT tag = data.getCompound(owner);
            if (!Strings.isNullOrEmpty(this.lastKey)) {
                tag.putString(chunkPopulator.getName(), this.lastKey);
            }
            data.put(owner, tag);
        }

        @Override
        public boolean generateFeature(IWorld world, int chunkX, int chunkZ, Random random, boolean retroGen) {
            boolean ret = chunkPopulator.retroGen(random, chunkX, chunkZ, world);
            this.lastKey = chunkPopulator.getGenKey();
            return ret;
        }

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
        public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, IFeatureGenerator<FC> feature, FC fc, Placement<PC> placement, PC pc) {
            addFeature(decorationStage, new FeatureWrapper<>(feature), fc, placement, pc);
        }

        @Override
        public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, Feature<FC> feature, FC fc, Placement<PC> placement, PC pc) {
            addFeature(decorationStage, new ConfiguredFeature<>(feature, fc), new ConfiguredPlacement<>(placement, pc));
        }

        @Override
        public <FC extends IFeatureConfig, PC extends IPlacementConfig> void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<FC> configuredFeature, ConfiguredPlacement<PC> placement) {
            Feature<DecoratedFeatureConfig> feature = WorldHelper.getFeature(configuredFeature) instanceof FlowersFeature ? Feature.DECORATED_FLOWER : Feature.DECORATED;
            ConfiguredFeature<?> f = new ConfiguredFeature<>(feature, new DecoratedFeatureConfig(configuredFeature, placement));
            addFeature(decorationStage, f);
        }

        @Override
        @SuppressWarnings("unchecked")
        public void addFeature(GenerationStage.Decoration decorationStage, ConfiguredFeature<? extends IFeatureConfig> feature) {
            if (WorldHelper.getFeature(feature) instanceof Structure) {
                WorldGenManager.this.structurez.add((Structure<?>) WorldHelper.getFeature(feature));
            }
            if (WorldHelper.getFeatureConfiguration(feature) instanceof IRetroGenFeatureConfig) {
                String name = Preconditions.checkNotNull(Optional.of(loadingMC).orElse(FMLHelper.getActiveModContainer())).getModId();
                String cName = name + ":" + ((IRetroGenFeatureConfig) WorldHelper.getFeatureConfiguration(feature)).getName();
                if (!retroGennableNames.add(cName)) {
                    throw new IllegalArgumentException("Feature generation name is already in use: " + cName);
                }

                feature = new RetroGenCapableCompositeFeature(WorldHelper.getFeature(feature), (IRetroGenFeatureConfig) WorldHelper.getFeatureConfiguration(feature), name);
                register((IChunkIOHook) feature);
            }
            biome.addFeature(decorationStage, feature);
        }

        @Override
        public <C extends IFeatureConfig> void addStructure(Structure<C> structure, C config, String s) {
            if (structures.containsKey(s)) {
                throw new IllegalArgumentException("Structure name " + s + " is already taken!");
            }
            structures.put(s, structure);
            WorldGenManager.this.registeredFeatures.add(Triple.of(structure, config, s));
            biome.addStructure(structure, config);
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, WorldCarver<C> carver, C carverConfig) {
            addCarver(stage, new ConfiguredCarver<>(carver, carverConfig));
        }

        @Override
        public <C extends ICarverConfig> void addCarver(GenerationStage.Carving stage, ConfiguredCarver<C> carver) {
            biome.addCarver(stage, carver);
        }

        @Override
        public void addSpawn(EntityClassification type, EntityType<? extends LivingEntity> entityType, int weight, int minGroupCount, int maxGroupCount) {
            addSpawn(type, new Biome.SpawnListEntry(entityType, weight, maxGroupCount, maxGroupCount));
        }

        @Override
        public void addSpawn(EntityClassification type, Biome.SpawnListEntry spawnListEntry) {
            WorldHelper.addBiomeSpawnEntry(biome, type, spawnListEntry);
        }

    }

    private static class RetroGenCapableCompositeFeature<C extends IRetroGenFeatureConfig> extends ConfiguredFeature<C> implements IChunkIOHook, ILegacyFeatureGenerator {

        private RetroGenCapableCompositeFeature(Feature<C> feature, C fc, String owner) {
            super(feature, fc);
            Feature<C> rf;
            if (feature instanceof FeatureWrapper) {
                rf = ((FeatureWrapper<C>) feature).getRetroGen();
            } else {
                rf = feature;
            }
            this.retroGenfeature = new ConfiguredFeature<>(rf, fc);
            this.owner = owner;
            this.config = fc;
        }

        private final C config;
        private final ConfiguredFeature<C> retroGenfeature;
        private final String owner;
        private String lastKey;

        @Override
        public boolean place(@Nonnull IWorld world, @Nonnull ChunkGenerator<? extends GenerationSettings> chunkGenerator, @Nonnull Random random, @Nonnull BlockPos blockPos) {
            boolean b = super.place(world, chunkGenerator, random, blockPos);
            this.lastKey = config.getGenKey();
            return b;
        }

        @Override
        public String getName() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void chunkLoadedFromDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager) {
            CompoundNBT tag = data.getCompound(owner);
            boolean b = tag.contains(config.getName());
            if ((!b || !config.getGenKey().equals(tag.getString(config.getName()))) && config.shouldRetroGen(b)) {
                worldGenManager.registerForRetroGen(Preconditions.checkNotNull(chunk.getWorldForge()), chunk.getPos(), this);
            } else {
                this.lastKey = config.getGenKey();
            }
        }

        @Override
        public void chunkSavedToDisk(IChunk chunk, CompoundNBT data, IWorldGenManager worldGenManager) {
            CompoundNBT tag = data.getCompound(owner);
            if (!Strings.isNullOrEmpty(this.lastKey)) {
                tag.putString(config.getName(), this.lastKey);
            }
            data.put(owner, tag);
        }

        @Override
        public boolean generateFeature(IWorld world, int chunkX, int chunkZ, Random random, boolean retroGen) {
            if (!retroGen) {
                throw new IllegalArgumentException();
            }

            //Mimic ChunkGenerator
            BlockPos pos = new BlockPos(chunkX * 16, 0, chunkZ * 16).add(8, 8, 8);

            boolean ret = retroGenfeature.place(world, world.getChunkProvider().getChunkGenerator(), random, pos);
            this.lastKey = config.getGenKey();
            return ret;
        }

    }

    private static class FeatureWrapper<C extends IFeatureConfig> extends Feature<C> {

        private FeatureWrapper(IFeatureGenerator<C> feature) {
            this(feature, false);
        }

        private FeatureWrapper(IFeatureGenerator<C> feature, boolean retroGen) {
            super(feature::deserialize);
            this.feature = feature;
            this.retroGen = retroGen;
        }

        private final IFeatureGenerator<C> feature;
        private final boolean retroGen;


        @Override
        public boolean place(@Nonnull IWorld world, @Nonnull ChunkGenerator<? extends GenerationSettings> chunkGenerator, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull C config) {
            return feature.generateFeature(world, pos, chunkGenerator, random, config, retroGen);
        }

        private Feature<C> getRetroGen() {
            return new FeatureWrapper<>(feature, true);
        }

    }

    static {
        MinecraftForge.EVENT_BUS.register(INSTANCE);
    }

}