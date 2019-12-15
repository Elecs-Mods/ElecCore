package elec332.core.client.model.loading;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332 on 22-3-2017.
 */
public interface IBlockModelItemLink {

    public BlockState getRenderState(ItemStack s);

    default public boolean itemInheritsModel() {
        return true;
    }

}
