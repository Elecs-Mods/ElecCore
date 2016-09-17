package elec332.core.compat.forestry;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IAlleleSpecies;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public interface IGenomeTemplate<S extends IAlleleSpecies> {

    public IGenomeTemplate<S> setSpecies(S species);

    @Nonnull
    public IGenomeTemplate copy();

    @Nonnull
    public IAllele[] getAlleles();

    /**
     * Used to get the species from a genome.
     *
     * @param alleles The genome
     * @return The species defined the the genome
     */
    public S getSpecies(IAllele[] alleles);

}
