package elec332.core.compat.forestry;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.genetics.*;
import forestry.apiculture.flowers.Flower;
import forestry.apiculture.flowers.FlowerProvider;
import forestry.core.genetics.alleles.AlleleHelper;
import forestry.core.genetics.alleles.EnumAllele;
import forestry.core.genetics.alleles.IAlleleValue;
import net.minecraft.util.math.Vec3i;

/**
 * Created by Elec332 on 15-8-2016.
 */
public class ForestryAlleles {

    public static final IAlleleBoolean TRUE_RECESSIVE, FALSE_RECESSIVE;
    public static final IAlleleInteger LIFESPAN_SHORTEST, LIFESPAN_SHORTER, LIFESPAN_SHORT, LIFESPAN_SHORTENED,
            LIFESPAN_NORMAL, LIFESPAN_ELONGATED, LIFESPAN_LONG, LIFESPAN_LONGER, LIFESPAN_LONGEST;
    public static final IAlleleFloat SPEED_SLOWEST, SPEED_SLOWER, SPEED_SLOW, SPEED_NORMAL, SPEED_FAST,
            SPEED_FASTER, SPEED_FASTEST;
    public static final IAlleleTolerance TOLERANCE_NONE, TOLERANCE_BOTH_1, TOLERANCE_BOTH_2, TOLERANCE_BOTH_3,
            TOLERANCE_BOTH_4, TOLERANCE_BOTH_5, TOLERANCE_UP_1, TOLERANCE_UP_2, TOLERANCE_UP_3, TOLERANCE_UP_4,
            TOLERANCE_UP_5, TOLERANCE_DOWN_1, TOLERANCE_DOWN_2, TOLERANCE_DOWN_3, TOLERANCE_DOWN_4, TOLERANCE_DOWN_5;
    public static final IAlleleInteger FLOWERING_SLOWEST, FLOWERING_SLOWER, FLOWERING_SLOW, FLOWERING_AVERAGE,
            FLOWERING_FAST, FLOWERING_FASTER, FLOWERING_FASTEST, FLOWERING_MAXIMUM;
    public static final IAlleleArea TERRITORY_AVERAGE, TERRITORY_LARGE, TERRITORY_LARGER, TERRITORY_LARGEST;
    public static final IAlleleInteger FERTILITY_LOW, FERTILITY_NORMAL, FERTILITY_HIGH, FERTILITY_MAXIMUM;
    public static final IAlleleFlowers FLOWERS_VANILLA, FLOWERS_NETHER, FLOWERS_CACTI, FLOWERS_MUSHROOMS,
            FLOWERS_END, FLOWERS_JUNGLE, FLOWERS_SNOW, FLOWERS_WHEAT, FLOWERS_GOURD;


    private static IAllele getForestryAllele(String name) {
        return AlleleManager.alleleRegistry.getAllele("forestry." + name);
    }

    private static IAlleleFlowers getAlleleFl(IAlleleValue<FlowerProvider> flowerProviderIAlleleValue){
        return (IAlleleFlowers) getAllele(EnumBeeChromosome.FLOWER_PROVIDER, flowerProviderIAlleleValue);
    }

    private static IAlleleArea getAlleleA(IAlleleValue<Vec3i> areaIAlleleValue){
        return (IAlleleArea) getAllele(EnumBeeChromosome.TERRITORY, areaIAlleleValue);
    }

    private static IAlleleInteger getAlleleI(IAlleleValue<Integer> integerIAlleleValue){
        return (IAlleleInteger) getAllele(EnumBeeChromosome.FLOWERING, integerIAlleleValue);
    }

    private static IAlleleTolerance getAlleleT(IAlleleValue<EnumTolerance> toleranceIAlleleValue){
        return (IAlleleTolerance) getAllele(EnumBeeChromosome.HUMIDITY_TOLERANCE, toleranceIAlleleValue);
    }

    private static IAllele getAllele(EnumBeeChromosome chromosome, IAlleleValue value){
        AlleleHelper.instance.set(cache, chromosome, value);
        return cache[chromosome.ordinal()];
    }

    private static IAllele[] cache;

    static {
        cache = new IAllele[EnumBeeChromosome.values().length];
        TRUE_RECESSIVE = (IAlleleBoolean) getForestryAllele("boolFalse");
        FALSE_RECESSIVE = (IAlleleBoolean) getForestryAllele("boolTrue");
        LIFESPAN_SHORTEST = (IAlleleInteger) getForestryAllele("lifespanShortest");
        LIFESPAN_SHORTER = (IAlleleInteger) getForestryAllele("lifespanShorter");
        LIFESPAN_SHORT = (IAlleleInteger) getForestryAllele("lifespanShort");
        LIFESPAN_SHORTENED = (IAlleleInteger) getForestryAllele("lifespanShortened");
        LIFESPAN_NORMAL = (IAlleleInteger) getForestryAllele("lifespanNormal");
        LIFESPAN_ELONGATED = (IAlleleInteger) getForestryAllele("lifespanElongated");
        LIFESPAN_LONG = (IAlleleInteger) getForestryAllele("lifespanLong");
        LIFESPAN_LONGER = (IAlleleInteger) getForestryAllele("lifespanLonger");
        LIFESPAN_LONGEST = (IAlleleInteger) getForestryAllele("lifespanLongest");
        SPEED_SLOWEST = (IAlleleFloat) getForestryAllele("speedSlowest");
        SPEED_SLOWER = (IAlleleFloat) getForestryAllele("speedSlower");
        SPEED_SLOW = (IAlleleFloat) getForestryAllele("speedSlow");
        SPEED_NORMAL = (IAlleleFloat) getForestryAllele("speedNormal");
        SPEED_FAST = (IAlleleFloat) getForestryAllele("speedFast");
        SPEED_FASTER = (IAlleleFloat) getForestryAllele("speedFaster");
        SPEED_FASTEST = (IAlleleFloat) getForestryAllele("speedFastest");
        TOLERANCE_NONE = getAlleleT(EnumAllele.Tolerance.NONE);
        TOLERANCE_UP_1 = getAlleleT(EnumAllele.Tolerance.UP_1);
        TOLERANCE_UP_2 = getAlleleT(EnumAllele.Tolerance.UP_2);
        TOLERANCE_UP_3 = getAlleleT(EnumAllele.Tolerance.UP_3);
        TOLERANCE_UP_4 = getAlleleT(EnumAllele.Tolerance.UP_4);
        TOLERANCE_UP_5 = getAlleleT(EnumAllele.Tolerance.UP_5);
        TOLERANCE_DOWN_1 = getAlleleT(EnumAllele.Tolerance.DOWN_1);
        TOLERANCE_DOWN_2 = getAlleleT(EnumAllele.Tolerance.DOWN_2);
        TOLERANCE_DOWN_3 = getAlleleT(EnumAllele.Tolerance.DOWN_3);
        TOLERANCE_DOWN_4 = getAlleleT(EnumAllele.Tolerance.DOWN_4);
        TOLERANCE_DOWN_5 = getAlleleT(EnumAllele.Tolerance.DOWN_5);
        TOLERANCE_BOTH_1 = getAlleleT(EnumAllele.Tolerance.BOTH_1);
        TOLERANCE_BOTH_2 = getAlleleT(EnumAllele.Tolerance.BOTH_2);
        TOLERANCE_BOTH_3 = getAlleleT(EnumAllele.Tolerance.BOTH_3);
        TOLERANCE_BOTH_4 = getAlleleT(EnumAllele.Tolerance.BOTH_4);
        TOLERANCE_BOTH_5 = getAlleleT(EnumAllele.Tolerance.BOTH_5);
        FLOWERING_SLOWEST = getAlleleI(EnumAllele.Flowering.SLOWEST);
        FLOWERING_SLOWER = getAlleleI(EnumAllele.Flowering.SLOWER);
        FLOWERING_SLOW = getAlleleI(EnumAllele.Flowering.SLOW);
        FLOWERING_AVERAGE = getAlleleI(EnumAllele.Flowering.AVERAGE);
        FLOWERING_FAST = getAlleleI(EnumAllele.Flowering.FAST);
        FLOWERING_FASTER = getAlleleI(EnumAllele.Flowering.FASTER);
        FLOWERING_FASTEST = getAlleleI(EnumAllele.Flowering.FASTEST);
        FLOWERING_MAXIMUM = getAlleleI(EnumAllele.Flowering.MAXIMUM);
        TERRITORY_AVERAGE = getAlleleA(EnumAllele.Territory.AVERAGE);
        TERRITORY_LARGE = getAlleleA(EnumAllele.Territory.LARGE);
        TERRITORY_LARGER = getAlleleA(EnumAllele.Territory.LARGER);
        TERRITORY_LARGEST = getAlleleA(EnumAllele.Territory.LARGEST);
        FERTILITY_LOW = getAlleleI(EnumAllele.Fertility.LOW);
        FERTILITY_NORMAL = getAlleleI(EnumAllele.Fertility.NORMAL);
        FERTILITY_HIGH = getAlleleI(EnumAllele.Fertility.HIGH);
        FERTILITY_MAXIMUM = getAlleleI(EnumAllele.Fertility.MAXIMUM);
        FLOWERS_VANILLA = getAlleleFl(EnumAllele.Flowers.VANILLA);
        FLOWERS_NETHER = getAlleleFl(EnumAllele.Flowers.NETHER);
        FLOWERS_CACTI = getAlleleFl(EnumAllele.Flowers.CACTI);
        FLOWERS_MUSHROOMS = getAlleleFl(EnumAllele.Flowers.MUSHROOMS);
        FLOWERS_END = getAlleleFl(EnumAllele.Flowers.END);
        FLOWERS_JUNGLE = getAlleleFl(EnumAllele.Flowers.JUNGLE);
        FLOWERS_SNOW = getAlleleFl(EnumAllele.Flowers.SNOW);
        FLOWERS_WHEAT = getAlleleFl(EnumAllele.Flowers.WHEAT);
        FLOWERS_GOURD = getAlleleFl(EnumAllele.Flowers.GOURD);
    }

}
