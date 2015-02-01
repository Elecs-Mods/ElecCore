package elec332.core.api.dimension;

/**
 * Created by Elec332 on 29-1-2015.
 */
import elec332.core.helper.RegisterHelper;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class Igniter extends Item {
    public Igniter(CreativeTabs cTab, int DimID, String name) {
        this.setUnlocalizedName(name);
        this.setCreativeTab(cTab);
        this.setMaxStackSize(1);
        this.DimID = DimID;
        RegisterHelper.registerItem(this, name);
    }

    int DimID;

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer player, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        ++par5;
        if (player.getCurrentEquippedItem() != null) {
            if (player.canPlayerEdit(par4, par5, par6, par7, par1ItemStack) && par3World.getBlock(par4, par5, par6).getMaterial() == Material.air) {
                if (DimensionAPI.getPortalFromDIM(DimID).tryToCreatePortal(par3World, par4, par5, par6) && par3World.isRemote) {
                    player.inventory.decrStackSize(player.inventory.currentItem, 1);
                }
                return true;
            }
        }
        return false;
    }
}
