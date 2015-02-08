package elec332.core.main;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;

/**
 * Created by Elec332.
 */
public class ElecCTab {

	public static CreativeTabs ElecTab = new CreativeTabs("Elecs_Mods") {
	    @Override
	    @SideOnly(Side.CLIENT)
	    public Item getTabIconItem() {
	        return Item.getItemFromBlock(Blocks.anvil);
	    }
	};
}
