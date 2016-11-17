package elec332.core.main;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;

/**
 * Created by Elec332.
 */
public class ElecCTab {

	public static CreativeTabs ElecTab = new CreativeTabs("Elecs_Mods") {

		@Override
	    @SideOnly(Side.CLIENT)
		@Nonnull
	    public ItemStack getTabIconItem() {
	        return new ItemStack(Item.getItemFromBlock(Blocks.ANVIL));
	    }

	};

}
