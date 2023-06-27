package elec332.core.api.inventory;

/**
 * Created by Elec332 on 20-5-2015.
 * <p>
 * Used for 'things' that have a progress bar in their GUI
 */
public interface IHasProgressBar {

    /**
     * @return The progress of this machine, can be any value.
     */
    int getProgress();

    /**
     * Used to get a scaled value of the progress (between 0.0f and 1.0f)
     *
     * @param progress The progress of this machine
     * @return The scaled progress of this machine (between 0.0f and 1.0f)
     */
    float getProgressScaled(int progress);

}
