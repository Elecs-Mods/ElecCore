package elec332.core.data;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;

import java.util.function.Supplier;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractBlockStateProvider extends BlockStateProvider {

    public AbstractBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected final void registerStatesAndModels() {
        registerBlockStatesAndModels();
    }

    protected abstract void registerBlockStatesAndModels();

    public void simpleBlock(Supplier<Block> block) {
        simpleBlock(block.get());
    }

}
