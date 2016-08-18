package elec332.core.compat.forestry.allele;

import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;
import forestry.api.genetics.IChromosomeType;
import forestry.api.genetics.IEffectData;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 14-8-2016.
 */
public abstract class AlleleEffectThrottled extends AlleleEffect {

    public AlleleEffectThrottled(ResourceLocation rl, IChromosomeType... types) {
        super(rl, types);
    }

    public AlleleEffectThrottled(String s, IChromosomeType... types) {
        super(s, types);
    }

    public AlleleEffectThrottled(String uid, String unlocalizedName, IChromosomeType... types) {
        super(uid, unlocalizedName, types);
    }

    private int throttle = 20;
    private boolean requiresWorkingQueen = false;

    public AlleleEffectThrottled setThrottle(int throttle){
        this.throttle = throttle;
        return this;
    }

    public AlleleEffectThrottled setRequiresWorkingQueen(){
        this.requiresWorkingQueen = true;
        return this;
    }

    //Code below was taken from Forestry, the original code can be found at: https://github.com/ForestryMC/ForestryMC

    @Override
    public final IEffectData doEffect(IBeeGenome genome, IEffectData storedData, IBeeHousing housing) {
        if (isThrottled(storedData, housing)) {
            return storedData;
        }
        return doEffectThrottled(genome, storedData, housing);
    }

    private boolean isThrottled(IEffectData storedData, IBeeHousing housing) {

        if (requiresWorkingQueen && housing.getErrorLogic().hasErrors()) {
            return true;
        }

        int time = storedData.getInteger(0);
        time++;
        storedData.setInteger(0, time);

        if (time < throttle) {
            return true;
        }

        // Reset since we are done throttling.
        storedData.setInteger(0, 0);
        return false;
    }

    public abstract IEffectData doEffectThrottled(IBeeGenome genome, IEffectData storedData, IBeeHousing housing);

}
