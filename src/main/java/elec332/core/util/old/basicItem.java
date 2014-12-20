package elec332.core.util.old;

import elec332.core.helper.registerHelper;
import elec332.core.main.ElecCTab;
import net.minecraft.item.Item;

public class basicItem extends Item{
	
	public static Item Item;

	@Deprecated
	protected static void CreateItem(Item item, String Itemname, int MaxStackSize) {
		SetStack(item, MaxStackSize);
		item.setCreativeTab(ElecCTab.ElecTab);
		item.setUnlocalizedName("Elec." + Itemname);
		item.setTextureName("Elec:" + Itemname);
		registerHelper.registerItem(item, Itemname);
	}
	
	private static void SetStack(Item item, int MaxStackSize){
		item.setMaxStackSize(MaxStackSize);
	}

}
