package elec332.core.data;

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractItemTagsProvider extends ItemTagsProvider {

    public AbstractItemTagsProvider(DataGenerator generatorIn, BlockTagsProvider blockTagsProvider) {
        super(generatorIn, blockTagsProvider);
    }

    @Override
    protected final void registerTags() {
        registerItemTags();
    }

    protected abstract void registerItemTags();

}
