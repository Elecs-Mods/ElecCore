package elec332.core.util.Regist;

import cpw.mods.fml.common.registry.GameRegistry;
import elec332.core.main.ElecCTab;
import net.minecraft.item.Item;

public class ItemRegist extends Item{
	
	public static Item Item;

	private static void registerItem(Item mItem, String name) {
		GameRegistry.registerItem(mItem, name);
	}
	
	protected static void CreateItem(Item item, String Itemname, int MaxStackSize) {
		SetStack(item, MaxStackSize);
		item.setCreativeTab(ElecCTab.ElecTab);
		item.setUnlocalizedName("Elec." + Itemname);
		item.setTextureName("Elec:" + Itemname);
        registerItem(item, Itemname);
	}
	
	private static void SetStack(Item item, int MaxStackSize){
		item.setMaxStackSize(MaxStackSize);
	}

}
