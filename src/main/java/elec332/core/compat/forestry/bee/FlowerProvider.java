package elec332.core.compat.forestry.bee;

import com.google.common.base.Predicate;
import elec332.core.util.StatCollector;
import forestry.api.genetics.ICheckPollinatable;
import forestry.api.genetics.IFlowerProvider;
import forestry.api.genetics.IIndividual;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by Elec332 on 24-8-2016.
 */
@SuppressWarnings("all")
public class FlowerProvider implements IFlowerProvider {

    public FlowerProvider(@Nonnull String flowerType){
        this(flowerType, DEFAULT_PREDICATE);
    }

    public FlowerProvider(@Nonnull String flowerType, @Nonnull String unlocalizedName){
        this(flowerType, unlocalizedName, DEFAULT_PREDICATE);
    }

    public FlowerProvider(@Nonnull String flowerType, @Nonnull Predicate<Pair<World, ICheckPollinatable>> predicate){
        this(flowerType, "flowerprovider."+flowerType, predicate);
    }

    public FlowerProvider(@Nonnull String flowerType, @Nonnull String unlocalizedName, @Nonnull Predicate<Pair<World, ICheckPollinatable>> predicate){
        this.flowerType = flowerType;
        this.unlocalizedName = unlocalizedName;
        this.predicate = predicate;
    }

    private static final Predicate<Pair<World, ICheckPollinatable>> DEFAULT_PREDICATE;
    private final String flowerType, unlocalizedName;
    private final Predicate<Pair<World, ICheckPollinatable>> predicate;

    @Override
    public boolean isAcceptedPollinatable(World world, ICheckPollinatable iCheckPollinatable) {
        return predicate.apply(Pair.of(world, iCheckPollinatable));
    }

    @Override
    public String getFlowerType() {
        return flowerType;
    }

    @Override
    public String getDescription() {
        return StatCollector.translateToLocal(unlocalizedName);
    }

    @Override
    public NonNullList<ItemStack> affectProducts(World world, IIndividual iIndividual, BlockPos blockPos, NonNullList<ItemStack> itemStacks) {
        return itemStacks;
    }

    static {
        DEFAULT_PREDICATE = new Predicate<Pair<World, ICheckPollinatable>>() {

            @Override
            public boolean apply(@Nullable Pair<World, ICheckPollinatable> input) {
                return true;
            }

        };
    }

}
