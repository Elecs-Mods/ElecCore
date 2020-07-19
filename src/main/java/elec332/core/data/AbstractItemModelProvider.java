package elec332.core.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelProvider;

/**
 * Created by Elec332 on 14-7-2020
 */
public abstract class AbstractItemModelProvider extends ItemModelProvider {

    public AbstractItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected final void registerModels() {
        registerItemModels();
    }

    protected abstract void registerItemModels();

}
