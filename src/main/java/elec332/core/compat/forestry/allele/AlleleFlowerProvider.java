package elec332.core.compat.forestry.allele;

import forestry.api.apiculture.EnumBeeChromosome;
import forestry.api.apiculture.FlowerManager;
import forestry.api.genetics.IAlleleFlowers;
import forestry.api.genetics.IFlowerAcceptableRule;
import forestry.api.genetics.IFlowerGrowthRule;
import forestry.api.genetics.IFlowerProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 24-8-2016.
 */
public class AlleleFlowerProvider extends AbstractAllele implements IAlleleFlowers {

    public AlleleFlowerProvider(ResourceLocation rl, IFlowerProvider flowerProvider) {
        super(rl, EnumBeeChromosome.FLOWER_PROVIDER);
        this.flowerProvider = flowerProvider;
    }

    public AlleleFlowerProvider(String s, IFlowerProvider flowerProvider) {
        super(s, EnumBeeChromosome.FLOWER_PROVIDER);
        this.flowerProvider = flowerProvider;
    }

    public AlleleFlowerProvider(String uid, String unlocalizedName, IFlowerProvider flowerProvider) {
        super(uid, unlocalizedName, EnumBeeChromosome.FLOWER_PROVIDER);
        this.flowerProvider = flowerProvider;
    }

    private final IFlowerProvider flowerProvider;

    @Override
    public IFlowerProvider getProvider() {
        return this.flowerProvider;
    }

    public void registerGrowthRule(IFlowerGrowthRule iFlowerGrowthRule) {
        FlowerManager.flowerRegistry.registerGrowthRule(iFlowerGrowthRule, flowerProvider.getFlowerType());
    }

    public void registerAcceptableFlower(Block block) {
        FlowerManager.flowerRegistry.registerAcceptableFlower(block, flowerProvider.getFlowerType());
    }

    public void registerAcceptableFlower(IBlockState iBlockState) {
        FlowerManager.flowerRegistry.registerAcceptableFlower(iBlockState, flowerProvider.getFlowerType());
    }

    public void registerAcceptableFlowerRule(IFlowerAcceptableRule iFlowerAcceptableRule) {
        FlowerManager.flowerRegistry.registerAcceptableFlowerRule(iFlowerAcceptableRule, flowerProvider.getFlowerType());
    }

    public void registerPlantableFlower(IBlockState iBlockState, double v) {
        FlowerManager.flowerRegistry.registerPlantableFlower(iBlockState, v, flowerProvider.getFlowerType());
    }

}
