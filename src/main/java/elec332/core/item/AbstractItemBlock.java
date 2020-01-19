package elec332.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractItemBlock extends BlockItem implements IAbstractItem {

    public AbstractItemBlock(Block block, Properties itemBuilder) {
        super(block, itemBuilder);
    }

    public static final String TILE_DATA_TAG = "BlockEntityTag";

    private String unlocalizedName;

    @Nonnull
    @Override
    public String getDefaultTranslationKey() {
        if (this.unlocalizedName == null) {
            unlocalizedName = ItemMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Nonnull
    @Override
    public String getTranslationKey(ItemStack stack) {
        return ItemMethods.getUnlocalizedName(stack, this);
    }

}
