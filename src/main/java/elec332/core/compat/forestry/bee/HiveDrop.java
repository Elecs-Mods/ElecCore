package elec332.core.compat.forestry.bee;

import elec332.core.compat.forestry.IIndividualDefinition;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 20-8-2016.
 */
public class HiveDrop extends forestry.apiculture.genetics.HiveDrop {

    public HiveDrop(IIndividualDefinition<IBeeGenome, ?, ?> individual, double chance, ItemStack... extraDrops){
        this(individual.getGenome(), chance, extraDrops);
    }

    public HiveDrop(IBeeGenome genome, double chance, ItemStack... extraDrops){
        super(chance, null, extraDrops);
        this.genome = genome;
    }

    private final IBeeGenome genome;

    public HiveDrop setIgnobleChance(double chance) {
        super.setIgnobleShare(chance);
        return this;
    }

    @Override
    @Nonnull
    public IBee getBeeType(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return BeeManager.beeRoot.getBee(genome);
    }

}
