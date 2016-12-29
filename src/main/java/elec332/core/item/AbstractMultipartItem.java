package elec332.core.item;

import mcmultipart.api.item.ItemBlockMultipart;
import mcmultipart.api.multipart.IMultipart;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 22-9-2016.
 */
public abstract class AbstractMultipartItem<T extends Block & IMultipart> extends ItemBlockMultipart {

    public AbstractMultipartItem(T t){
        super(t);
        setRegistryName(t.getRegistryName());
        setUnlocalizedNameFromName();
    }

    protected void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(@Nonnull Item itemIn, @Nonnull CreativeTabs tab, @Nonnull List<ItemStack> subItems) {
        getSubItems(itemIn, subItems, tab);
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(@Nonnull Item item, @Nonnull List<ItemStack> subItem, CreativeTabs creativeTab){
        super.getSubItems(item, creativeTab, subItem);
    }

    @Override
    public int getMetadata(int damage) {
        return damage;
    }

}
