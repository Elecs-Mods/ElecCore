package elec332.core.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractBlockTagsProvider extends BlockTagsProvider {

    public AbstractBlockTagsProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected final void registerTags() {
        registerBlockTags();
    }

    protected abstract void registerBlockTags();

}
