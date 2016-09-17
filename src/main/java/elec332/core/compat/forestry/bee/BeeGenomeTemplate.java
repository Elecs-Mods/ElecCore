package elec332.core.compat.forestry.bee;

import elec332.core.compat.forestry.ForestryAlleles;
import elec332.core.compat.forestry.IGenomeTemplate;
import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.IAlleleBeeSpecies;
import forestry.api.genetics.*;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static forestry.api.apiculture.EnumBeeChromosome.*;

/**
 * Created by Elec332 on 14-8-2016.
 */
public class BeeGenomeTemplate implements IGenomeTemplate<IAlleleBeeSpecies> {

    public BeeGenomeTemplate(){
        alleles = getDefaultTemplate();
    }

    private IAllele[] alleles;

    @Override
    public BeeGenomeTemplate setSpecies(IAlleleBeeSpecies species){
        setAllele(SPECIES, species);
        return this;
    }

    public BeeGenomeTemplate setSpeed(IAlleleFloat speed){
        setAllele(SPEED, speed);
        return this;
    }

    public BeeGenomeTemplate setLifeSpan(IAlleleInteger lifeSpan){
        setAllele(LIFESPAN, lifeSpan);
        return this;
    }

    public BeeGenomeTemplate setFertility(IAlleleInteger fertility){
        setAllele(FERTILITY, fertility);
        return this;
    }

    public BeeGenomeTemplate setTemperatureTolerance(IAlleleTolerance temperatureTolerance){
        setAllele(TEMPERATURE_TOLERANCE, temperatureTolerance);
        return this;
    }

    public BeeGenomeTemplate setNeverSleeps(IAlleleBoolean neverSleeps){
        setAllele(NEVER_SLEEPS, neverSleeps);
        return this;
    }

    public BeeGenomeTemplate setHumidityTolerance(IAlleleTolerance humidityTolerance){
        setAllele(HUMIDITY_TOLERANCE, humidityTolerance);
        return this;
    }

    public BeeGenomeTemplate setToleratesRain(IAlleleBoolean toleratesRain){
        setAllele(TOLERATES_RAIN, toleratesRain);
        return this;
    }

    public BeeGenomeTemplate setCaveDwelling(IAlleleBoolean caveDwelling){
        setAllele(CAVE_DWELLING, caveDwelling);
        return this;
    }

    public BeeGenomeTemplate setFlowerProvider(IAlleleFlowers flowerProvider){
        setAllele(FLOWER_PROVIDER, flowerProvider);
        return this;
    }

    public BeeGenomeTemplate setFloweringSpeed(IAlleleInteger floweringSpeed){
        setAllele(FLOWERING, floweringSpeed);
        return this;
    }

    public BeeGenomeTemplate setTerritory(IAlleleArea territory){
        setAllele(TERRITORY, territory);
        return this;
    }

    public BeeGenomeTemplate setEffect(IAlleleEffect effect){
        setAllele(EFFECT, effect);
        return this;
    }

    @Override
    @Nonnull
    public BeeGenomeTemplate copy(){
        BeeGenomeTemplate ret = new BeeGenomeTemplate();
        System.arraycopy(alleles, 0, ret.alleles, 0, alleles.length);
        return ret;
    }

    @Override
    @Nonnull
    public IAllele[] getAlleles(){
        return alleles;
    }

    @Override
    public IAlleleBeeSpecies getSpecies(IAllele[] alleles) {
        return (IAlleleBeeSpecies) alleles[EnumBeeChromosome.SPECIES.ordinal()];
    }

    private void setAllele(EnumBeeChromosome chromosome, IAllele allele){
        alleles[chromosome.ordinal()] = allele;
    }

    private static IAllele[] defaultTemplate;

    private static IAllele[] getDefaultTemplate() {
        if (defaultTemplate == null) {
            defaultTemplate = new IAllele[EnumBeeChromosome.values().length];

            defaultTemplate[EnumBeeChromosome.SPEED.ordinal()] = ForestryAlleles.SPEED_SLOWEST;
            defaultTemplate[EnumBeeChromosome.LIFESPAN.ordinal()] = ForestryAlleles.LIFESPAN_SHORTER;
            defaultTemplate[EnumBeeChromosome.FERTILITY.ordinal()] = ForestryAlleles.FERTILITY_NORMAL;
            defaultTemplate[EnumBeeChromosome.TEMPERATURE_TOLERANCE.ordinal()] = ForestryAlleles.TOLERANCE_NONE;
            defaultTemplate[EnumBeeChromosome.NEVER_SLEEPS.ordinal()] = ForestryAlleles.FALSE_RECESSIVE;
            defaultTemplate[EnumBeeChromosome.HUMIDITY_TOLERANCE.ordinal()] = ForestryAlleles.TOLERANCE_NONE;
            defaultTemplate[EnumBeeChromosome.TOLERATES_RAIN.ordinal()] = ForestryAlleles.FALSE_RECESSIVE;
            defaultTemplate[EnumBeeChromosome.CAVE_DWELLING.ordinal()] = ForestryAlleles.FALSE_RECESSIVE;
            defaultTemplate[EnumBeeChromosome.FLOWER_PROVIDER.ordinal()] = ForestryAlleles.FLOWERS_VANILLA;
            defaultTemplate[EnumBeeChromosome.FLOWERING.ordinal()] = ForestryAlleles.FLOWERING_SLOWEST;
            defaultTemplate[EnumBeeChromosome.TERRITORY.ordinal()] = ForestryAlleles.TERRITORY_AVERAGE;
            defaultTemplate[EnumBeeChromosome.EFFECT.ordinal()] = forestry.apiculture.genetics.alleles.AlleleEffect.effectNone;
        }
        return Arrays.copyOf(defaultTemplate, defaultTemplate.length);
    }

}
