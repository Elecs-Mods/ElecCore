package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

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

    public static boolean tryDrainItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
            IFluidHandler item = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack stack1 = item.drain(capacity, false);
            if (stack1 == null){
                return false;
            }
            int i = fluidHandler.fill(stack1, false);
            if (i > 0) {
                if (player.getEntityWorld().isRemote){
                    return true;
                }
                fluidHandler.fill(item.drain(i, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        return false;
    }

    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        return tryFillItem(player, hand, tank instanceof IFluidHandler ? (IFluidHandler) tank : FluidTankWrapper.of(tank), tank.getCapacity());
    }

    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
            IFluidHandler item = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            FluidStack fluid = fluidHandler.drain(capacity, false);
            if (fluid == null){
                return false;
            }
            int i = item.fill(fluid, false);
            if (i > 0){
                if (player.getEntityWorld().isRemote){
                    return true;
                }
                item.fill(fluidHandler.drain(capacity, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        return false;
    }

}
