package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.capabilities.OptionalCapabilityInstance;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 15-10-2016.
 */
public class FluidHelper {

    /**
     * Gets the {@link IFluidHandler} at the specified location, if it exists
     *
     * @param iba    The world
     * @param pos    The position to check
     * @param facing The side to check
     * @return The {@link IFluidHandler} at the specified location, if it exists
     */
    @Nullable
    public static IFluidHandler getFluidHandler(IBlockReader iba, BlockPos pos, EnumFacing facing) {
        TileEntity tile = WorldHelper.getTileAt(iba, pos);
        return tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing).orElse(null);
    }

    /**
     * Handles when a player right-clicks the specified tank
     *
     * @param player The player that clicked the tank
     * @param hand   The hand the player used
     * @param tank   The tank being clicked
     * @return Whether something happened
     */
    public static boolean onTankActivated(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        IFluidHandler fluidHandler = FluidTankWrapper.of(tank);
        return tryDrainItem(player, hand, fluidHandler, tank.getCapacity()) || tryFillItem(player, hand, fluidHandler, tank.getCapacity());
    }

    /**
     * Handles when a player right-clicks the specified {@link IFluidHandler}
     *
     * @param player       The player that clicked the tank
     * @param hand         The hand the player used
     * @param fluidHandler The fluid handler being clicked
     * @param tankCapacity The internal capacity of the {@link IFluidHandler}
     * @return Whether something happened
     */
    public static boolean onTankActivated(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int tankCapacity) {
        return tryDrainItem(player, hand, fluidHandler, tankCapacity) || tryFillItem(player, hand, fluidHandler, tankCapacity);
    }

    /**
     * Attempts to drain the item the player holds into the tank
     *
     * @param player The player that right-clicked the tank
     * @param hand   The hand the player clicked with
     * @param tank   the tank to be filled
     * @return Whether something happened
     */
    public static boolean tryDrainItem(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        return tryDrainItem(player, hand, tank instanceof IFluidHandler ? (IFluidHandler) tank : FluidTankWrapper.of(tank), tank.getCapacity());
    }

    /**
     * Attempts to drain the item the player holds into the tank
     *
     * @param player       The player that right-clicked the tank
     * @param hand         The hand the player clicked with
     * @param fluidHandler The fluid handler to be filled
     * @param capacity     The internal capacity of the {@link IFluidHandler}
     * @return Whether something happened
     */
    public static boolean tryDrainItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        OptionalCapabilityInstance<IFluidHandler> ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (!ofh.isPresent()) {
            ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        }
        if (ofh.isPresent()) {
            IFluidHandler item = ofh.orElseThrow(NullPointerException::new);
            FluidStack stack1 = item.drain(capacity, false);
            if (stack1 == null) {
                return false;
            }
            int i = fluidHandler.fill(stack1, false);
            if (i > 0) {
                if (player.getEntityWorld().isRemote) {
                    return true;
                }
                fluidHandler.fill(item.drain(i, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to fill the item the player holds with contents from the tank
     *
     * @param player The player that right-clicked the tank
     * @param hand   The hand the player clicked with
     * @param tank   the tank to be drained
     * @return Whether something happened
     */
    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidTank tank) {
        return tryFillItem(player, hand, tank instanceof IFluidHandler ? (IFluidHandler) tank : FluidTankWrapper.of(tank), tank.getCapacity());
    }

    /**
     * Attempts to fill the item the player holds with contents from the tank
     *
     * @param player       The player that right-clicked the tank
     * @param hand         The hand the player clicked with
     * @param fluidHandler The fluid handler to be drained
     * @param capacity     The internal capacity of the {@link IFluidHandler}
     * @return Whether something happened
     */
    public static boolean tryFillItem(EntityPlayer player, EnumHand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        OptionalCapabilityInstance<IFluidHandler> ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (!ofh.isPresent()) {
            ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        }
        if (ofh.isPresent()) {
            IFluidHandler item = ofh.orElseThrow(NullPointerException::new);
            FluidStack fluid = fluidHandler.drain(capacity, false);
            if (fluid == null) {
                return false;
            }
            int i = item.fill(fluid, false);
            if (i > 0) {
                if (player.getEntityWorld().isRemote) {
                    return true;
                }
                item.fill(fluidHandler.drain(capacity, !PlayerHelper.isPlayerInCreative(player)), true);
                return true;
            }
        }
        return false;
    }

}
