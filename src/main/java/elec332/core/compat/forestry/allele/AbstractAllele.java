package elec332.core.compat.forestry.allele;

import elec332.core.util.StatCollector;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 14-8-2016.
 */
public abstract class AbstractAllele implements IAllele {

    public AbstractAllele(ResourceLocation rl, IChromosomeType... types){
        this(rl.toString(), rl.toString().replace(":", ".allele."), types);
    }

    public AbstractAllele(String s, IChromosomeType... types){
        this(s, s, types);
    }

    public AbstractAllele(String uid, String unlocalizedName, IChromosomeType... types){
        this.uid = uid;
        this.unlocalizedName = unlocalizedName;
        AlleleManager.alleleRegistry.registerAllele(this, types);
    }

    public AbstractAllele setDominant(){
        this.dominant = true;
        return this;
    }

    private final String uid, unlocalizedName;
    private boolean dominant;

    @Override
    public String getUID() {
        return uid;
    }

    @Override
    public boolean isDominant() {
        return false;
    }

    @Override
    public String getName() {
        return StatCollector.translateToLocal(getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName() {
        return unlocalizedName;
    }

}
