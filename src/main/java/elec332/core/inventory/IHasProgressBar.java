package elec332.core.inventory;

/**
 * Created by Elec332 on 20-5-2015.
 */
public interface IHasProgressBar {

    public int getProgress();

    public void setProgress(int i);

    public float getProgressScaled();

    public boolean isWorking();

}
