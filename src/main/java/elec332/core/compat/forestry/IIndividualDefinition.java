package elec332.core.compat.forestry;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IGenome;
import forestry.api.genetics.IIndividual;
import forestry.api.genetics.ISpeciesType;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public interface IIndividualDefinition<G extends IGenome, I extends IIndividual, T extends ISpeciesType> {

    @Nonnull
    public G getGenome();

    @Nonnull
    public IAllele[] getAlleles();

    public I getIndividual();

    public ItemStack getMemberStack(T speciesType);

}
