package elec332.core.compat.forestry;

import forestry.api.genetics.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public final class DefaultIndividualDefinition<G extends IGenome, I extends IIndividual, T extends ISpeciesType> implements IIndividualDefinition<G, I, T> {

    @SuppressWarnings("unchecked")
    DefaultIndividualDefinition(IAllele[] alleles, ISpeciesRoot speciesRoot){
        this.alleles = alleles;
        this.speciesRoot = speciesRoot;
        this.g = (G) this.speciesRoot.templateAsGenome(this.alleles);
    }

    private final G g;
    private final IAllele[] alleles;
    private final ISpeciesRoot speciesRoot;

    @Nonnull
    @Override
    public G getGenome() {
        return g;
    }

    @Nonnull
    @Override
    public IAllele[] getAlleles(){
        IAllele[] ret = new IAllele[alleles.length];
        System.arraycopy(alleles, 0, ret, 0, alleles.length);
        return ret;
    }

    @Override
    @SuppressWarnings("unchecked")
    public I getIndividual() {
        return (I) speciesRoot.templateAsIndividual(alleles);
    }

    @Override
    public ItemStack getMemberStack(T speciesType) {
        return speciesRoot.getMemberStack(getIndividual(), speciesType);
    }

}
