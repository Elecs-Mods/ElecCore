package elec332.core.util;

import elec332.core.world.WorldHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.util.LazyOptional;
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
    public static IFluidHandler getFluidHandler(IBlockReader iba, BlockPos pos, Direction facing) {
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
    public static boolean onTankActivated(PlayerEntity player, Hand hand, IFluidTank tank) {
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
    public static boolean onTankActivated(PlayerEntity player, Hand hand, IFluidHandler fluidHandler, int tankCapacity) {
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
    public static boolean tryDrainItem(PlayerEntity player, Hand hand, IFluidTank tank) {
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
    public static boolean tryDrainItem(PlayerEntity player, Hand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        LazyOptional<IFluidHandler> ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (!ofh.isPresent()) {
            ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        }
        if (ofh.isPresent()) {
            IFluidHandler item = ofh.orElseThrow(NullPointerException::new);
            FluidStack stack1 = item.drain(capacity, IFluidHandler.FluidAction.SIMULATE);
            if (stack1 == null || stack1.isEmpty()) {
                return false;
            }
            int i = fluidHandler.fill(stack1, IFluidHandler.FluidAction.SIMULATE);
            if (i > 0) {
                if (player.getEntityWorld().isRemote) {
                    return true;
                }
                fluidHandler.fill(item.drain(i, PlayerHelper.isPlayerInCreative(player) ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
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
    public static boolean tryFillItem(PlayerEntity player, Hand hand, IFluidTank tank) {
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
    public static boolean tryFillItem(PlayerEntity player, Hand hand, IFluidHandler fluidHandler, int capacity) {
        if (fluidHandler == null) {
            return false;
        }
        ItemStack stack = player.getHeldItem(hand);
        if (!ItemStackHelper.isStackValid(stack)) {
            return false;
        }
        ///////Start
        LazyOptional<IFluidHandler> ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
        if (!ofh.isPresent()) {
            ofh = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        }
        if (ofh.isPresent()) {
            IFluidHandler item = ofh.orElseThrow(NullPointerException::new);
            FluidStack fluid = fluidHandler.drain(capacity, IFluidHandler.FluidAction.SIMULATE);
            if (fluid == null || fluid.isEmpty()) {
                return false;
            }
            int i = item.fill(fluid, IFluidHandler.FluidAction.SIMULATE);
            if (i > 0) {
                if (player.getEntityWorld().isRemote) {
                    return true;
                }
                item.fill(fluidHandler.drain(capacity, PlayerHelper.isPlayerInCreative(player) ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
        }
        return false;
    }

}
