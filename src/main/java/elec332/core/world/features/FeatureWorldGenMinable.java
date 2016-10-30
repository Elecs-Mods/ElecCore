package elec332.core.world.features;

import com.google.common.base.Predicate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockMatcher;
import net.minecraft.init.Blocks;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.config.Configuration;

/**
 * Created by Elec332 on 17-10-2016.
 */
public class FeatureWorldGenMinable extends FeatureWorldGenerator {

    public static FeatureWorldGenMinable newOreGenerator(String name, int times, int maxY, int clusterSize, IBlockState state, Predicate<IBlockState> predicate){
        return newOreGenerator(name, clusterSize, times, state, predicate).setMaxY(maxY);
    }

    public static FeatureWorldGenMinable newOreGenerator(String name, int times, int clusterSize, IBlockState state, Predicate<IBlockState> predicate){
        return new FeatureWorldGenMinable(name, times, new WorldGenMinable(state, clusterSize, predicate));
    }

    public static FeatureWorldGenMinable wrap(String name, int times, WorldGenMinable worldGenMinable){
        return new FeatureWorldGenMinable(name, times, worldGenMinable);
    }

    public FeatureWorldGenMinable(String name, int times, WorldGenMinable worldGenMinable) {
        super(name, times, worldGenMinable);
    }

    public FeatureWorldGenMinable setClusterSize(int newSize){
        this.worldGenMinable = new WorldGenMinable(((WorldGenMinable)worldGenMinable).oreBlock, Math.min(newSize, getMaxClusterSize()), ((WorldGenMinable) worldGenMinable).predicate);
        return this;
    }

    @Override
    public FeatureWorldGenMinable setTimes(int maxY) {
        return (FeatureWorldGenMinable) super.setTimes(maxY);
    }

    @Override
    public FeatureWorldGenMinable setGenerationMultiplier(float multiplier) {
        return (FeatureWorldGenMinable) super.setGenerationMultiplier(multiplier);
    }

    @Override
    public FeatureWorldGenMinable setMaxY(int maxY) {
        return (FeatureWorldGenMinable) super.setMaxY(maxY);
    }

    @Override
    public FeatureWorldGenMinable setShouldGen(boolean shouldGen) {
        return (FeatureWorldGenMinable) super.setShouldGen(shouldGen);
    }

    protected int getMaxClusterSize(){
        return 30;
    }

    @Override
    public void reconfigure(Configuration config) {
        super.reconfigure(config);
        int old = ((WorldGenMinable)worldGenMinable).numberOfBlocks;
        int size = config.getInt("clusterSize", getConfigCategoryName(), old, 0, getMaxClusterSize(), "Sets the max cluster size.");
        if (size != old){
            setClusterSize(size);
        }
    }

    public static final Predicate<IBlockState> STONE, NETHERRACK, ENDSTONE;

    static {
        STONE = BlockMatcher.forBlock(Blocks.STONE);
        NETHERRACK = BlockMatcher.forBlock(Blocks.NETHERRACK);
        ENDSTONE = BlockMatcher.forBlock(Blocks.END_STONE);
    }

}
