package elec332.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractItemBlock extends ItemBlock implements IAbstractItem {

    public AbstractItemBlock(Block block) {
        super(block);
    }

    private String unlocalizedName;

    @Nonnull
    @Override
    public String getUnlocalizedName() {
        if (this.unlocalizedName == null) {
            unlocalizedName = ItemMethods.createUnlocalizedName(this);
        }
        return unlocalizedName;
    }

    @Nonnull
    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return ItemMethods.getUnlocalizedName(stack, this);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
