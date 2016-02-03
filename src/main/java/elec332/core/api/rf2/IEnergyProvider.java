package elec332.core.api.rf2;

/**
 * Created by Elec332 on 3-2-2016.
 */
public interface IEnergyProvider {

    /**
     * Remove energy from an IEnergyProvider, internal distribution is left entirely to the IEnergyProvider.
     *
     * @param maxExtract Maximum amount of energy to extract.
     * @param simulate If true, the extraction will only be simulated.
     * @return Amount of energy that was (or would have been, if simulated) extracted.
     */
    int extractEnergy(int maxExtract, boolean simulate);

    /**
     * @return the amount of energy currently stored.
     */
    int getEnergyStored();

    /**
     * @return the maximum amount of energy that can be stored.
     */
    int getMaxEnergyStored();

}
