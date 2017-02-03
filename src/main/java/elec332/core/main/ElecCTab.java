package elec332.core.main;

import elec332.core.util.AbstractCreativeTab;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by Elec332.
 */
public class ElecCTab {

	public static CreativeTabs ElecTab = AbstractCreativeTab.create("Elecs_Mods", new ItemStack(Item.getItemFromBlock(Blocks.ANVIL)));

}
