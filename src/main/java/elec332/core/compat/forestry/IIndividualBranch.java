package elec332.core.compat.forestry;

import forestry.api.genetics.IAlleleSpeciesBuilder;
import forestry.api.genetics.IClassification;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public interface IIndividualBranch<T extends IGenomeTemplate> {

    public void setBranchProperties(T genomeTemplate);

    //You'll have to cast this one yourself
    default public void setIndividualProperties(IAlleleSpeciesBuilder speciesBuilder){
    }

    @Nonnull
    public IClassification getClassification();

}
