package elec332.core.util;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

/**
 * Created by Elec332 on 11-8-2016.
 */
public abstract class FluidTankWrapper implements IFluidHandler, IFluidTank, INBTSerializable<NBTTagCompound> {

    public static FluidTankWrapper withCapacity(int capacity){
        return of(new FluidTank(capacity));
    }

    public static FluidTankWrapper of(final IFluidTank tank){
        return new FluidTankWrapper() {

            @Override
            protected IFluidTank getTank() {
                return tank;
            }

        };
    }

    public FluidTankWrapper(){
        final IFluidTankProperties prop = new Properties(this);
        this.properties = new IFluidTankProperties[]{
                prop
        };
    }

    private IFluidTankProperties[] properties;

    protected abstract IFluidTank getTank();

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource == null){
            return 0;
        }
        FluidStack f = getTank().getFluid();
        if (!(f == null || f.isFluidEqual(resource))){
            return 0;
        }
        if (canFillFluidType(resource)){
            return getTank().fill(resource, doFill);
        }
        return 0;
    }


    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        FluidStack f = getTank().getFluid();
        if (resource == null || !resource.isFluidEqual(f)) {
            return null;
        }
        return drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidStack f = getTank().getFluid();
        if (f == null || !canDrainFluidType(f)) {
            return null;
        }
        return getTank().drain(maxDrain, doDrain);
    }

    @Nullable
    @Override
    public FluidStack getFluid() {
        FluidStack tankStack = getTank().getFluid();
        return tankStack == null ? null : tankStack.copy();
    }

    @Override
    public int getFluidAmount() {
        return getTank().getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return getTank().getCapacity();
    }

    @Override
    public FluidTankInfo getInfo() {
        return getTank().getInfo();
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
    public NBTTagCompound serializeNBT() {
        if (getTank() instanceof FluidTankWrapper){
            return ((FluidTankWrapper) getTank()).serializeNBT();
        } else if (getTank() instanceof FluidTank){
            return ((FluidTank) getTank()).writeToNBT(new NBTTagCompound());
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        if (getTank() instanceof FluidTankWrapper){
            ((FluidTankWrapper) getTank()).deserializeNBT(nbt);
        } else if (getTank() instanceof FluidTank){
            ((FluidTank) getTank()).readFromNBT(nbt);
        }
        throw new UnsupportedOperationException();
    }

    private class Properties implements IFluidTankProperties {

        private Properties(FluidTankWrapper tank){
            this.tank = tank;
        }

        private final FluidTankWrapper tank;

        @Nullable
        @Override
        public FluidStack getContents() {
            FluidStack stack = tank.getTank().getFluid();
            return stack == null ? null : stack.copy();
        }

        @Override
        public int getCapacity() {
            return tank.getTank().getCapacity();
        }

        @Override
        public boolean canFill() {
            return tank.canFill();
        }

        @Override
        public boolean canDrain() {
            return tank.canDrain();
        }

        @Override
        public boolean canFillFluidType(FluidStack fluidStack) {
            return tank.canFillFluidType(fluidStack);
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluidStack) {
            return tank.canDrainFluidType(fluidStack);
        }

    }

}
