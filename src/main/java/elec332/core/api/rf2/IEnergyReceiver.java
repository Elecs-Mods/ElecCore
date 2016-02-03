package elec332.core.api.rf2;

/**
 * Created by Elec332 on 3-2-2016.
 */
public interface IEnergyReceiver {

    /**
     * Add energy to an IEnergyReceiver, internal distribution is left entirely to the IEnergyReceiver.
     *
     * @param maxReceive Maximum amount of energy to receive.
     * @param simulate If true, the charge will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) received.
     */
    int receiveEnergy(int maxReceive, boolean simulate);

    /**
     * @return The amount of energy currently stored.
     */
    int getEnergyStored();

    /**
     * @return The maximum amount of energy that can be stored.
     */
    int getMaxEnergyStored();

}
