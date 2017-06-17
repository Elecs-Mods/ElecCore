package elec332.core.mcabstractionlayer.object;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Created by Elec332 on 29-1-2017.
 */
public interface IAbstractedItem {

    public void addInformationC(@Nonnull ItemStack stack, World world, List<String> tooltip, boolean advanced);

    @Nonnull
    public ActionResult<ItemStack> onItemRightClickC(EntityPlayer player, @Nonnull EnumHand hand, World world);

    @Nonnull
    public EnumActionResult onItemUseFirstC(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ);

    @Nonnull
    public EnumActionResult onItemUseC(EntityPlayer player, EnumHand hand, World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ);

    @SideOnly(Side.CLIENT)
    public void getSubItemsC(@Nonnull Item item, List<ItemStack> subItems, CreativeTabs creativeTab);

}
