package elec332.core.compat.forestry.bee;

import elec332.core.compat.forestry.IIndividualTemplate;
import forestry.api.apiculture.*;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public interface IBeeTemplate extends IIndividualTemplate<BeeGenomeTemplate, IAlleleBeeSpeciesBuilder, IBeeGenome, IBee, EnumBeeType, IBeeRoot> {

    public int getPrimaryColor();

    default public int getSecondaryColor(){
        return getPrimaryColor();
    }

    @Nonnull
    @Override
    default public Class<BeeGenomeTemplate> getGenomeTemplateType(){
        return BeeGenomeTemplate.class;
    }

    @Nonnull
    @Override
    default public IBeeRoot getSpeciesRoot(){
        return BeeManager.beeRoot;
    }

    @Nonnull
    @Override
    default public IAlleleBeeSpeciesBuilder getSpeciesBuilder(){
        return BeeManager.beeFactory.createSpecies(getUid(), isDominant(), getAuthority(), getUnlocalizedName(), getUnlocalizedDescription(), getIndividualBranch().getClassification(), getBinominalName(), getPrimaryColor(), getSecondaryColor());
    }

}
