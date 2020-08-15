package elec332.core.data;

import com.google.common.base.Preconditions;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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

    public static final ResourceLocation BUILTIN_ENTITY = new ResourceLocation("eleccore", "item/entity");
    public static final ResourceLocation ITEM_GENERATED = new ResourceLocation("item/generated");
    public static final ResourceLocation EMPTY_MODEL = new ResourceLocation("item/air");

    @Override
    protected final void registerModels() {
        registerItemModels();
    }

    protected abstract void registerItemModels();

    public void cubeAll(Supplier<? extends Item> item, String texture) {
        cubeAll(Preconditions.checkNotNull(item.get().getRegistryName()).getPath(), modLoc(texture));
    }

    public void simpleModel(Supplier<? extends Item> item) {
        simpleModel(item, "item/" + Preconditions.checkNotNull(item.get().getRegistryName()).getPath());
    }

    public void simpleModel(Supplier<? extends Item> item, String... textures) {
        ItemModelBuilder imb = this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(this.getExistingFile(this.mcLoc("item/generated")));
        for (int i = 0; i < textures.length; i++) {
            imb.texture("layer" + i, textures[i]);
        }
    }

    public ItemModelBuilder parentedModel(Supplier<? extends Block> block) {
        return parentedModel(() -> block.get().asItem(), "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
    }

    public ItemModelBuilder parentedModel(Supplier<? extends Item> item, Supplier<Block> block) {
        return parentedModel(item, "block/" + Preconditions.checkNotNull(block.get().getRegistryName()).getPath());
    }

    public ItemModelBuilder parentedModel(Supplier<? extends Item> item, String parent) {
        return parentedModel(item, this.modLoc(parent));
    }

    public ItemModelBuilder parentedModel(Block block, ResourceLocation parent) {
        return parentedModel(block::asItem, parent);
    }

    public ItemModelBuilder parentedModel(Supplier<? extends Item> item, ResourceLocation parent) {
        return this.getBuilder(Preconditions.checkNotNull(item.get().getRegistryName()).getPath()).parent(new ModelFile.UncheckedModelFile(parent));
    }

}
