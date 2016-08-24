package elec332.core.compat.forestry;

import forestry.api.genetics.*;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public interface IIndividualTemplate<T extends IGenomeTemplate, B extends IAlleleSpeciesBuilder, G extends IGenome, I extends IIndividual, S extends ISpeciesType, R extends ISpeciesRoot> extends IIndividualDefinition<G, I, S> {

    @Nonnull
    public String getUid();

    public boolean isDominant();

    @Nonnull
    default public String getAuthority(){
        return "Elec332";
    }

    @Nonnull
    public String getUnlocalizedName();

    @Nonnull
    default public String getUnlocalizedDescription(){
        return getUnlocalizedName()+".description";
    }

    @Nonnull
    public String getBinominalName();

    @Nonnull
    public Class<T> getGenomeTemplateType();

    @Nonnull
    public R getSpeciesRoot();

    @Nonnull
    public B getSpeciesBuilder();

    @Nonnull
    public IIndividualBranch<T> getIndividualBranch();

    public void modifyGenomeTemplate(T template);

    public void setSpeciesProperties(B speciesBuilder);

    public void registerMutations();

    public void setIndividualDefinition(IIndividualDefinition<G, I, S> iIndividualDefinition);

    public IIndividualDefinition<G, I, S> getIndividualDefinition();

    @Nonnull
    @Override
    default public G getGenome(){
        return getIndividualDefinition().getGenome();
    }

    @Nonnull
    @Override
    default public IAllele[] getAlleles(){
        return getIndividualDefinition().getAlleles();
    }

    @Override
    default public I getIndividual(){
        return getIndividualDefinition().getIndividual();
    }

    @Override
    default public ItemStack getMemberStack(S speciesType){
        return getIndividualDefinition().getMemberStack(speciesType);
    }

}
