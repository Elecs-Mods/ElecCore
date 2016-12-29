package elec332.core.item;

import mcmultipart.item.ItemMultiPart;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 22-9-2016.
 */
public abstract class AbstractOldMultipartItem extends ItemMultiPart {

    public AbstractOldMultipartItem(){
        this(null);
    }

    public AbstractOldMultipartItem(ResourceLocation rl){
        if (rl != null) {
            setRegistryName(rl);
            setUnlocalizedNameFromName();
        }
    }

    public void setUnlocalizedNameFromName(){
        setUnlocalizedName(getRegistryName().toString().replace(":", ".").toLowerCase());
    }


    @Override
    @SideOnly(Side.CLIENT)
    public final void getSubItems(@Nonnull Item itemIn, CreativeTabs tab, List<ItemStack> subItems) {
        getSubItems(itemIn, subItems, tab);
    }

    @SideOnly(Side.CLIENT)
    protected void getSubItems(@Nonnull Item item, List<ItemStack> subItems, CreativeTabs creativeTab){
        super.getSubItems(item, creativeTab, subItems);
    }


}
