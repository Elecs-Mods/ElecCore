package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.wrappers.FluidHandlerWrapper;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 15-10-2016.
 */
@SuppressWarnings("deprecation")
public class FluidHelper {

    @Nullable
    @SuppressWarnings("deprecation")
    public static IFluidHandler getFluidHandler(IBlockAccess iba, BlockPos pos, EnumFacing facing){
        TileEntity tile = WorldHelper.getTileAt(iba, pos);
        if (tile.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing)){
            return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing);
        } else if (tile instanceof net.minecraftforge.fluids.IFluidHandler) {
            return new FluidHandlerWrapper((net.minecraftforge.fluids.IFluidHandler) tile, facing);
        }
        return null;
    }

    public static boolean onTankActivated(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        IFluidHandler fluidHandler = FluidTankWrapper.of(tank);
        return tryDrainItem(player, hand, fluidHandler, tank.getCapacity()) || tryFillItem(player, hand, fluidHandler, tank.getCapacity());
    }

    public static boolean onTankActivated(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int tankCapacity) {
        return tryDrainItem(player, hand, fluidHandler, tankCapacity) || tryFillItem(player, hand, fluidHandler, tankCapacity);
    }

    public static boolean tryDrainItem(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        return tryDrainItem(player, hand, tank instanceof IFluidHandler ? (IFluidHandler) tank : FluidTankWrapper.of(tank), tank.getCapacity());
    }

    @SuppressWarnings("all")
    public static boolean tryDrainItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (stack == null) {
            return false;
        }
        ///////Start
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
            IFluidHandler item = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (item == null){
                return false;
            }
            FluidStack stack1 = item.drain(capacity, false);
            if (stack1 == null){
                return false;
            }
            int i = fluidHandler.fill(stack1, false);
            if (i > 0) {
                if (player.worldObj.isRemote){
                    return true;
                }
                fluidHandler.fill(item.drain(i, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem item = (IFluidContainerItem) stack.getItem();
            FluidStack fluidStack = item.getFluid(stack);
            if (fluidStack == null) {
                return false;
            } else {
                int i = fluidHandler.fill(fluidStack, false);
                if (i == fluidStack.amount) {
                    if (player.worldObj.isRemote){
                        return true;
                    }
                    fluidHandler.fill(item.drain(stack, i, !PlayerHelper.isPlayerInCreative(player)), true);
                    return true;
                }
            }
        }
        if (FluidContainerRegistry.isFilledContainer(stack)) {
            FluidStack stack1 = FluidContainerRegistry.getFluidForFilledItem(stack);
            if (stack1 != null) {
                int i = fluidHandler.fill(stack1.copy(), false);
                if (i == stack1.amount) {
                    if (player.worldObj.isRemote){
                        return true;
                    }
                    fluidHandler.fill(stack1.copy(), true);
                    ItemStack s = FluidContainerRegistry.drainFluidContainer(stack);
                    if (!PlayerHelper.isPlayerInCreative(player)) {
                        player.setHeldItem(hand, s == null ? null : s.copy());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        return tryFillItem(player, hand, tank instanceof IFluidHandler ? (IFluidHandler) tank : FluidTankWrapper.of(tank), tank.getCapacity());
    }

    @SuppressWarnings("all")
    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (stack == null) {
            return false;
        }
        ///////Start
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
            IFluidHandler item = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (item == null){
                return false;
            }
            FluidStack fluid = fluidHandler.drain(capacity, false);
            if (fluid == null){
                return false;
            }
            int i = item.fill(fluid, false);
            if (i > 0){
                if (player.worldObj.isRemote){
                    return true;
                }
                item.fill(fluidHandler.drain(capacity, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem item = (IFluidContainerItem) stack.getItem();
            int itemCapacity = item.getCapacity(stack);
            FluidStack fluidStack = item.getFluid(stack);
            if (fluidStack == null) {
                FluidStack fs = fluidHandler.drain(itemCapacity, false);
                if (item.fill(stack, fs, false) == itemCapacity) {

                    item.fill(stack, fluidHandler.drain(itemCapacity, !PlayerHelper.isPlayerInCreative(player)), true);
                    return true;
                }
            } else {
                return false;
            }
        }
        if (FluidContainerRegistry.isEmptyContainer(stack)) {
            ItemStack s = FluidContainerRegistry.fillFluidContainer(fluidHandler.drain(1000, false), stack.copy());
            if (s != null) {
                FluidStack f = FluidContainerRegistry.getFluidForFilledItem(s.copy());
                if (f != null && f.amount == 1000) {
                    if (player.worldObj.isRemote){
                        return true;
                    }
                    fluidHandler.drain(1000, true);
                    if (!PlayerHelper.isPlayerInCreative(player)) {
                        player.setHeldItem(hand, s.copy());
                    }
                    return true;
                }
            }
        }
        return false;
    }

}
