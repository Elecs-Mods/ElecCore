package elec332.core.compat.forestry.allele;

import elec332.core.compat.forestry.ForestryInit;
import forestry.api.apiculture.*;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IEffectData;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * Created by Elec332 on 14-8-2016.
 */
public abstract class AlleleEffect extends AbstractAllele implements IAlleleBeeEffect {

    public AlleleEffect(ResourceLocation rl, IChromosomeType... types) {
        super(rl, types);
    }

    public AlleleEffect(String s, IChromosomeType... types) {
        super(s, types);
    }

    public AlleleEffect(String uid, String unlocalizedName, IChromosomeType... types) {
        super(uid, unlocalizedName, types);
    }

    private boolean combinable;

    public AlleleEffect setCombinable(){
        this.combinable = true;
        return this;
    }

    @Override
    public abstract IEffectData validateStorage(IEffectData storedData);

    @Override
    public abstract IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

    @Override
    public IEffectData doFX(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        ForestryInit.forestryEffectNone.doFX(genome, storedData, housing);
        return storedData;
    }

    @Override
    public boolean isCombinable() {
        return combinable;
    }

    public static AxisAlignedBB getBounding(IBeeGenome genome, IBeeHousing housing) {
        return forestry.apiculture.genetics.alleles.AlleleEffect.getBounding(genome, housing);
    }

    public static <T extends Entity> List<T> getEntitiesInRange(IBeeGenome genome, IBeeHousing housing, Class<T> entityClass) {
        return forestry.apiculture.genetics.alleles.AlleleEffect.getEntitiesInRange(genome, housing, entityClass);
    }

}
