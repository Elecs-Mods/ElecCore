package elec332.core.api.rf2;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

import java.util.concurrent.Callable;

/**
 * Created by Elec332 on 3-2-2016.
 */
public class RF2API {

    @CapabilityInject(IEnergyReceiver.class)
    public static Capability<IEnergyReceiver> RECEIVER_CAPABILITY;
    @CapabilityInject(IEnergyProvider.class)
    public static Capability<IEnergyProvider> PROVIDER_CAPABILITY;

    static {
        CapabilityManager.INSTANCE.register(IEnergyReceiver.class, new Capability.IStorage<IEnergyReceiver>() {

            @Override
            public NBTBase writeNBT(Capability<IEnergyReceiver> capability, IEnergyReceiver instance, EnumFacing side) {
                return new NBTTagCompound(); //A receiver doesn't necessarily store energy.
            }

            @Override
            public void readNBT(Capability<IEnergyReceiver> capability, IEnergyReceiver instance, EnumFacing side, NBTBase nbt) {
            }

        }, new Callable<IEnergyReceiver>() {

            @Override
            public IEnergyReceiver call() throws Exception {
                throw new UnsupportedOperationException();
            }

        });
        CapabilityManager.INSTANCE.register(IEnergyProvider.class, new Capability.IStorage<IEnergyProvider>() {

            @Override
            public NBTBase writeNBT(Capability<IEnergyProvider> capability, IEnergyProvider instance, EnumFacing side) {
                return new NBTTagCompound(); //A provider doesn't necessarily store energy.
            }

            @Override
            public void readNBT(Capability<IEnergyProvider> capability, IEnergyProvider instance, EnumFacing side, NBTBase nbt) {
            }

        }, new Callable<IEnergyProvider>() {

            @Override
            public IEnergyProvider call() throws Exception {
                throw new UnsupportedOperationException();
            }

        });
    }

}
