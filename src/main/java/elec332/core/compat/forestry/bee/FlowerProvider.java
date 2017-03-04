package elec332.core.compat.forestry.bee;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import forestry.api.genetics.ICheckPollinatable;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 24-8-2016.
 */
@SuppressWarnings("all")
public class FlowerProvider extends forestry.apiculture.flowers.FlowerProvider {

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
        super(flowerType, unlocalizedName);
        this.predicate = predicate;
    }

    private static final Predicate<Pair<World, ICheckPollinatable>> DEFAULT_PREDICATE;
    private final Predicate<Pair<World, ICheckPollinatable>> predicate;

    @Override
    public boolean isAcceptedPollinatable(World world, ICheckPollinatable iCheckPollinatable) {
        return predicate.apply(Pair.of(world, iCheckPollinatable));
    }

    static {
        DEFAULT_PREDICATE = Predicates.alwaysTrue();
    }

}
