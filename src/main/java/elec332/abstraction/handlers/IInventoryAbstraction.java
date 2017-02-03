package elec332.abstraction.handlers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by Elec332 on 28-1-2017.
 */
public interface IInventoryAbstraction {

    public <E> List<E> createList();

    public <E> List<E> createList(int size, E defaultObj);

    public ItemStack getAndRemove(List<ItemStack> stacks, int index);

    public ItemStack getAndSplit(List<ItemStack> stacks, int index, int amount);

    public ItemStack loadItemStackFromNBT(NBTTagCompound tag);

    public int getSlotStackLimit(IItemHandler itemHandler, int slot);

    public ItemStack onPickupFromSlot(Slot slot, EntityPlayer player, ItemStack stack);

    public EnumActionResult fireOnItemUse(Item item, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ);

    public void updateCraftingInventory(IContainerListener listener, Container container, List<ItemStack> itemsList);

    public ItemStack getNullStack();

    public boolean isStackValid(ItemStack stack);

    @Nonnull
    public ItemStack copyItemStack(@Nullable ItemStack stack);

}
