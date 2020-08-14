package elec332.core.client.model.loading;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 22-3-2017.
 */
public interface IBlockModelItemLink {

    default BlockState getRenderState(ItemStack s) {
        return ((Block) this).getDefaultState();
    }

    default boolean itemInheritsModel() {
        return true;
    }

}
