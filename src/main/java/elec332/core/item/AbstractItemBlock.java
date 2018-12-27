package elec332.core.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Elec332 on 26-11-2016.
 */
@SuppressWarnings("all")
public class AbstractItemBlock extends ItemBlock {

    public AbstractItemBlock(Block block) {
        super(block);
    }

    public AbstractItemBlock(Block block, ResourceLocation rl) {
        this(block);
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
        }
    }

    public void setUnlocalizedNameFromName() {
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
