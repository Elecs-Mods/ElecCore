package elec332.core.data;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;

import java.util.function.Supplier;

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

    public void cubeAll(Supplier<Item> item, String texture) {
        cubeAll(Preconditions.checkNotNull(item.get().getRegistryName()).getPath(), modLoc(texture));
    }

    public void simpleModel(Supplier<Item> item) {
        simpleModel(item, "item/" + Preconditions.checkNotNull(item.get().getRegistryName()).getPath());
    }

    public void simpleModel(Supplier<Item> item, String... textures) {
        ItemModelBuilder imb = this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(this.getExistingFile(this.mcLoc("item/generated")));
        for (int i = 0; i < textures.length; i++) {
            imb.texture("layer" + i, textures[i]);
        }
    }

    public ItemModelBuilder parentedModel(Supplier<Block> block) {
        return parentedModel(() -> block.get().asItem(), "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
    }

    public ItemModelBuilder parentedModel(Supplier<Item> item, Supplier<Block> block) {
        return parentedModel(item, "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
    }

    public ItemModelBuilder parentedModel(Supplier<Item> item, String parent) {
        return this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(new ModelFile.UncheckedModelFile(this.modLoc(parent)));
    }

}
