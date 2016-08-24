package elec332.core.compat.forestry.bee;

import com.google.common.collect.Lists;
import elec332.core.compat.forestry.IIndividualDefinition;
import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBee;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IHiveDrop;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.Collection;
import java.util.List;

/**
 * Created by Elec332 on 20-8-2016.
 */
public class HiveDrop implements IHiveDrop {

    public HiveDrop(IIndividualDefinition<IBeeGenome, ?, ?> individual, double chance, ItemStack... extraDrops){
        this(individual.getGenome(), chance, extraDrops);
    }

    public HiveDrop(IBeeGenome genome, double chance, ItemStack... extraDrops){
        this.genome = genome;
        this.extraDrops = extraDrops;
        this.chance = chance;
        this.ignobleChance = 0;
    }

    private final IBeeGenome genome;
    private final ItemStack[] extraDrops;
    private double chance, ignobleChance;

    public HiveDrop setIgnobleChance(double chance) {
        this.ignobleChance = chance;
        return this;
    }

    @Override
    public IBee getBeeType(IBlockAccess iBlockAccess, BlockPos blockPos) {
        return BeeManager.beeRoot.getBee(genome);
    }

    @Override
    public Collection<ItemStack> getExtraItems(IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
        List<ItemStack> ret = Lists.newArrayList();
        for (ItemStack stack : extraDrops){
            ret.add(stack.copy());
        }
        return ret;
    }

    @Override
    public double getChance(IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
        return chance;
    }

    @Override
    public double getIgnobleChance(IBlockAccess iBlockAccess, BlockPos blockPos, int i) {
        return ignobleChance;
    }

}
