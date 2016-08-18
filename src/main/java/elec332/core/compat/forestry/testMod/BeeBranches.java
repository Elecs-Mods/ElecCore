package elec332.core.compat.forestry.testMod;

import elec332.core.compat.forestry.IIndividualBranch;
import elec332.core.compat.forestry.bee.BeeGenomeTemplate;
import forestry.api.apiculture.BeeManager;
import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IClassification;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 15-8-2016.
 */
public enum BeeBranches implements IIndividualBranch<BeeGenomeTemplate> {
    TEST;

    BeeBranches(){
        classification = BeeManager.beeFactory.createBranch("testBranch", "TesteryBranchery");
        AlleleManager.alleleRegistry.getClassification("family.apidae").addMemberGroup(classification);
    }

    private IClassification classification;

    @Override
    public void setBranchProperties(BeeGenomeTemplate genomeTemplate) {

    }

    @Nonnull
    @Override
    public IClassification getClassification() {
        return classification;
    }

}
