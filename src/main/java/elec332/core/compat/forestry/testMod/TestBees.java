package elec332.core.compat.forestry.testMod;

import elec332.core.compat.forestry.IIndividualBranch;
import elec332.core.compat.forestry.IIndividualDefinition;
import elec332.core.compat.forestry.bee.BeeGenomeTemplate;
import elec332.core.compat.forestry.bee.IBeeTemplate;
import forestry.api.apiculture.EnumBeeType;
import forestry.api.apiculture.IAlleleBeeSpeciesBuilder;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import org.fusesource.jansi.Ansi;

import javax.annotation.Nonnull;
import java.awt.*;

/**
 * Created by Elec332 on 15-8-2016.
 */
public enum TestBees implements IBeeTemplate {

    TEST;

    TestBees(){

    }

    private IIndividualDefinition<IBeeGenome, IBee, EnumBeeType> individual;

    @Override
    public int getPrimaryColor() {
        return Color.BLACK.getRGB();
    }

    @Nonnull
    @Override
    public String getUid() {
        return "test:testBee";
    }

    @Override
    public boolean isDominant() {
        return false;
    }

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        return "unlTestbee.name";
    }

    @Nonnull
    @Override
    public String getBinominalName() {
        return "difficultName";
    }

    @Nonnull
    @Override
    public IIndividualBranch<BeeGenomeTemplate> getIndividualBranch() {
        return BeeBranches.TEST;
    }

    @Override
    public void modifyGenomeTemplate(BeeGenomeTemplate template) {
        //Nope
    }

    @Override
    public void setSpeciesProperties(IAlleleBeeSpeciesBuilder speciesBuilder) {
        speciesBuilder.setNocturnal();
    }

    @Override
    public void registerMutations() {

    }

    @Override
    public void setIndividualDefinition(IIndividualDefinition<IBeeGenome, IBee, EnumBeeType> iIndividualDefinition) {
        individual = iIndividualDefinition;
    }

    @Override
    public IIndividualDefinition<IBeeGenome, IBee, EnumBeeType> getIndividualDefinition() {
        return individual;
    }

}
