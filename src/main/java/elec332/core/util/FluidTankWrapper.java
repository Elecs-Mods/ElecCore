package elec332.core.util;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.function.Supplier;

/**
 * Created by Elec332 on 11-8-2016.
 * <p>
 * An {@link IFluidHandler} wrapper for an {@link IFluidTank}
 */
public abstract class FluidTankWrapper implements IFluidHandler, IFluidTank, INBTSerializable<CompoundNBT> {

    /**
     * Creates a new fluid handler with the specified capacity
     */
    public static FluidTankWrapper withCapacity(int capacity) {
        return of(new FluidTank(capacity));
    }

    public static FluidTankWrapper of(final Supplier<IFluidTank> tank) {
        return new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return tank.get();
            }

        };
    }

    public static FluidTankWrapper of(final IFluidTank tank) {
        return new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return tank;
            }

        };
    }

    protected abstract IFluidTank getTank();

    @Override
    public int getTanks() {
        return 1;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        if (tank == 0) {
            return getFluid();
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (tank == 0) {
            return getCapacity();
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        if (tank == 0) {
            return isFluidValid(stack);
        }
        return false;
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return getTank().isFluidValid(stack) && canFillFluidType(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (resource == null || resource.isEmpty()) {
            return 0;
        }
        if (canFillFluidType(resource)) {
            return getTank().fill(resource, action);
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction doDrain) {
        if (resource == null || resource.isEmpty() || !canDrainFluidType(resource)) {
            return FluidStack.EMPTY;
        }
        return getTank().drain(resource, doDrain);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction doDrain) {
        FluidStack f = getFluid();
        if (canDrainFluidType(f)) {
            return getTank().drain(maxDrain, doDrain);
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    @SuppressWarnings("all")
    public FluidStack getFluid() {
        FluidStack tankStack = getTank().getFluid();
        return tankStack == null ? FluidStack.EMPTY : tankStack.copy();
    }

    @Override
    public int getFluidAmount() {
        return getTank().getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return getTank().getCapacity();
    }

    protected boolean canFill() {
        return true;
    }

    protected boolean canDrain() {
        return true;
    }

    protected boolean canFillFluidType(FluidStack fluidStack) {
        return canFill();
    }

    protected boolean canDrainFluidType(FluidStack fluidStack) {
        return canDrain();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (getTank() instanceof FluidTankWrapper) {
            return ((FluidTankWrapper) getTank()).serializeNBT();
        } else if (getTank() instanceof FluidTank) {
            return ((FluidTank) getTank()).writeToNBT(new CompoundNBT());
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (getTank() instanceof FluidTankWrapper) {
            ((FluidTankWrapper) getTank()).deserializeNBT(nbt);
        } else if (getTank() instanceof FluidTank) {
            ((FluidTank) getTank()).readFromNBT(nbt);
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
