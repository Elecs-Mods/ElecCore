package elec332.core.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332 on 26-11-2016.
 */
public abstract class AbstractCreativeTab extends CreativeTabs {

    public AbstractCreativeTab(String label) {
        super(label);
        initStack();
    }

    public AbstractCreativeTab(int index, String label) {
        super(index, label);
        initStack();
    }

    private void initStack(){
        if (FMLCommonHandler.instance().getSide().isClient()){
            clientStack = getDisplayStack();
        }
    }

    @SideOnly(Side.CLIENT)
    private ItemStack clientStack;

    @SideOnly(Side.CLIENT)
    @Override
    @Nonnull
    public final ItemStack getTabIconItem() {
        return clientStack;
    }

    @Nonnull
    @SideOnly(Side.CLIENT)
    protected abstract ItemStack getDisplayStack();

}
