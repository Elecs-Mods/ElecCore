package elec332.core.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.placement.*;

/**
 * Created by Elec332 on 31-12-2018
 */
public class FeaturePlacers {

    public static final Placement<FrequencyConfig> AT_SURFACE = Placement.AT_SURFACE;
    public static final Placement<FrequencyConfig> TOP_SOLID = Biome.TOP_SOLID;
    public static final Placement<FrequencyConfig> SURFACE_PLUS_32 = Biome.SURFACE_PLUS_32;
    public static final Placement<FrequencyConfig> TWICE_SURFACE = Biome.TWICE_SURFACE;
    public static final Placement<FrequencyConfig> AT_HEIGHT_64 = Biome.AT_HEIGHT_64;
    public static final Placement<NoiseDependant> SURFACE_PLUS_32_WITH_NOISE = Biome.SURFACE_PLUS_32_WITH_NOISE;
    public static final Placement<NoiseDependant> TWICE_SURFACE_WITH_NOISE = Biome.TWICE_SURFACE_WITH_NOISE;
    public static final Placement<NoPlacementConfig> PASSTHROUGH = Biome.PASSTHROUGH;
    public static final Placement<ChanceConfig> AT_SURFACE_WITH_CHANCE = Biome.AT_SURFACE_WITH_CHANCE;
    public static final Placement<ChanceConfig> TWICE_SURFACE_WITH_CHANCE = Biome.TWICE_SURFACE_WITH_CHANCE;
    public static final Placement<ChanceConfig> WITH_CHANCE = Biome.WITH_CHANCE;
    public static final Placement<ChanceConfig> TOP_SURFACE_WITH_CHANCE = Biome.TOP_SURFACE_WITH_CHANCE;
    public static final Placement<AtSurfaceWithExtraConfig> AT_SURFACE_WITH_EXTRA = Biome.AT_SURFACE_WITH_EXTRA;
    public static final Placement<CountRangeConfig> COUNT_RANGE = Biome.COUNT_RANGE;
    public static final Placement<CountRangeConfig> HEIGHT_BIASED_RANGE = Biome.HEIGHT_BIASED_RANGE;
    public static final Placement<CountRangeConfig> HEIGHT_VERY_BIASED_RANGE = Biome.HEIGHT_VERY_BIASED_RANGE;
    public static final Placement<CountRangeConfig> RANDOM_COUNT_WITH_RANGE = Biome.RANDOM_COUNT_WITH_RANGE;
    public static final Placement<ChanceRangeConfig> CHANCE_RANGE = Biome.CHANCE_RANGE;
    public static final Placement<HeightWithChanceConfig> AT_SUFACE_WITH_CHANCE_MULTIPLE = Biome.AT_SUFACE_WITH_CHANCE_MULTIPLE;
    public static final Placement<HeightWithChanceConfig> TWICE_SURFACE_WITH_CHANCE_MULTPLE = Biome.TWICE_SURFACE_WITH_CHANCE_MULTPLE;
    public static final Placement<DepthAverageConfig> DEPTH_AVERAGE = Biome.DEPTH_AVERAGE;
    public static final Placement<NoPlacementConfig> TOP_SOLID_ONCE = Biome.TOP_SOLID_ONCE;
    public static final Placement<TopSolidRangeConfig> TOP_SOLID_RANGE = Biome.TOP_SOLID_RANGE;
    public static final Placement<TopSolidWithNoiseConfig> TOP_SOLID_WITH_NOISE = Biome.TOP_SOLID_WITH_NOISE;
    public static final Placement<CaveEdgeConfig> CAVE_EDGE = Biome.CAVE_EDGE;
    public static final Placement<FrequencyConfig> AT_SURFACE_RANDOM_COUNT = Biome.AT_SURFACE_RANDOM_COUNT;
    public static final Placement<FrequencyConfig> NETHER_FIRE = Biome.NETHER_FIRE;
    public static final Placement<NoPlacementConfig> HEIGHT_4_TO_32 = Biome.HEIGHT_4_TO_32;
    public static final Placement<LakeChanceConfig> LAVA_LAKE = Biome.LAVA_LAKE;
    public static final Placement<LakeChanceConfig> LAKE_WATER = Biome.LAKE_WATER;
    public static final Placement<DungeonRoomConfig> DUNGEON_ROOM = Biome.DUNGEON_ROOM;
    public static final Placement<NoPlacementConfig> ROOFED_TREE = Biome.ROOFED_TREE;
    public static final Placement<ChanceConfig> ICEBERG_PLACEMENT = Biome.ICEBERG_PLACEMENT;
    public static final Placement<FrequencyConfig> NETHER_GLOWSTONE = Biome.NETHER_GLOWSTONE;

}
